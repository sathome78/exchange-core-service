package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.StopOrderDao;
import me.exrates.exception.NotCreatableOrderException;
import me.exrates.exception.OrderCancellingException;
import me.exrates.exception.StopOrderNoConditionException;
import me.exrates.model.dto.*;
import me.exrates.model.enums.*;
import me.exrates.model.main.CacheData;
import me.exrates.model.main.CurrencyPair;
import me.exrates.model.main.ExOrder;
import me.exrates.model.main.StopOrder;
import me.exrates.model.onlineTableDto.OrderWideListDto;
import me.exrates.service.*;
import me.exrates.util.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Log4j2
public class StopOrderServiceImpl implements StopOrderService {


    @Autowired
    private StopOrderDao stopOrderDao;
    @Autowired
    private OrderService orderService;
    @Autowired
    private StopOrdersHolder stopOrdersHolder;
    @Autowired
    private RatesHolder ratesHolder;
    @Autowired
    private TransactionDescription transactionDescription;
    @Autowired
    private WalletService walletService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserService userService;
    @Autowired
    private CurrencyService currencyService;

    private final static int THREADS_NUMBER = 50;
    private final static ExecutorService checkExecutors = Executors.newFixedThreadPool(THREADS_NUMBER);
    private ConcurrentMap<Integer, Object> buyLocks = new ConcurrentHashMap<>();
    private final static ExecutorService ordersExecutors = Executors.newFixedThreadPool(THREADS_NUMBER);
    private ConcurrentMap<Integer, Object> sellLocks = new ConcurrentHashMap<>();


    @Transactional
    @Override
    public String create(OrderCreateDto orderCreateDto, OrderActionEnum actionEnum, Locale locale) {
        Integer orderId = orderService.createOrder(orderCreateDto, actionEnum);
        if (orderId <= 0) {
            throw new NotCreatableOrderException(messageSource.getMessage("dberror.text", null, locale));
        }
        ExOrder exOrder = new ExOrder(orderCreateDto);
        exOrder.setId(orderId);
        this.onStopOrderCreate(exOrder);
        return "{\"result\":\"" + messageSource.getMessage("createdstoporder.text", null, locale) + "\"}";
    }


    @Transactional
    @Override
    public Integer createOrder(ExOrder exOrder) {
        StopOrder order = new StopOrder(exOrder);
        return stopOrderDao.create(order);
    }


    @Override
    public void proceedStopOrders(int pairId, NavigableSet<StopOrderSummaryDto> orders) {
        orders.forEach(p->{
            try {
                ordersExecutors.execute(new Runnable() {
                    @Override
                    public void run() {
                        proceedStopOrderAndRemove(p.getOrderId());
                    }
                });
            } catch (Exception e) {
                log.error("error processing stop order {}", e);
            }
        });
    }

    @Transactional
    @Override
    public void proceedStopOrderAndRemove(int stopOrderId) {
        OrderCreateDto stopOrder = null;
        stopOrder = getOrderById(stopOrderId, true);
        if (stopOrder == null || !stopOrder.getStatus().equals(OrderStatus.OPENED)) {
            throw new StopOrderNoConditionException(String.format(" order %s not found in db or illegal status ", stopOrderId));
        }
        stopOrdersHolder.delete(stopOrder.getCurrencyPair().getId(),
                new StopOrderSummaryDto(stopOrderId, stopOrder.getStop(), stopOrder.getOperationType()));
        try {
            this.proceedStopOrder(new ExOrder(stopOrder));
        } catch (Exception e) {
            log.error("error processing stop-order  {}", e);
            stopOrdersHolder.addOrder(new ExOrder(stopOrder));
        }
    }


    @Transactional
    private void proceedStopOrder(ExOrder exOrder) {
        OrderCreateDto newOrder = orderService.prepareNewOrder(currencyService.findCurrencyPairById(
                exOrder.getCurrencyPair().getId()), exOrder.getOperationType(),
                userService.getEmailById(exOrder.getUserId()), exOrder.getAmountBase(), exOrder.getExRate(), OrderBaseType.STOP_LIMIT);
        if (newOrder == null) {
            throw new RuntimeException("error preparing new order");
        }
        cancelCostsReserveForStopOrder(exOrder, Locale.ENGLISH, OrderActionEnum.ACCEPT);
        stopOrderDao.setStatus(exOrder.getId(), OrderStatus.CLOSED);
        Integer orderId = orderService.createOrderByStopOrder(newOrder, OrderActionEnum.CREATE, Locale.ENGLISH);
        if (orderId != null) {
            stopOrderDao.setStatusAndChildOrderId(exOrder.getId(), orderId, OrderStatus.CLOSED);
        }
    }

    @Transactional
    private void cancelCostsReserveForStopOrder(ExOrder dto, Locale locale, OrderActionEnum actionEnum) {
        WalletsForOrderCancelDto walletsForOrderCancelDto = walletService.getWalletForStopOrderByStopOrderIdAndOperationTypeAndBlock(
                dto.getId(), dto.getOperationType(), dto.getCurrencyPairId());
        OrderStatus currentStatus = OrderStatus.convert(walletsForOrderCancelDto.getOrderStatusId());
        if (currentStatus != OrderStatus.OPENED) {
            throw new OrderCancellingException(messageSource.getMessage("order.cannotcancel", null, locale));
        }
        WalletTransferStatus transferResult = walletService.walletInnerTransfer(
                walletsForOrderCancelDto.getWalletId(),
                walletsForOrderCancelDto.getReservedAmount(),
                TransactionSourceType.STOP_ORDER,
                walletsForOrderCancelDto.getOrderId(),
                transactionDescription.get(dto.getStatus(), actionEnum));
        if (transferResult != WalletTransferStatus.SUCCESS) {
            throw new OrderCancellingException(transferResult.toString());
        }
    }


    @Override
    @Transactional
    public boolean cancelOrder(ExOrder exOrder, Locale locale) {
        boolean res;
        cancelCostsReserveForStopOrder(exOrder, locale, OrderActionEnum.CANCEL);
        res = this.setStatus(exOrder.getId(), OrderStatus.CANCELLED);
        stopOrdersHolder.delete(exOrder.getCurrencyPairId(),
                new StopOrderSummaryDto(exOrder.getId(), exOrder.getStop(), exOrder.getOperationType()));
        return res;
    }


    @Transactional
    @Override
    public boolean setStatus(int orderId, OrderStatus status) {
        return stopOrderDao.setStatus(orderId, status);
    }

    @Override
    public OrderCreateDto getOrderById(Integer orderId, boolean forUpdate) {
        return stopOrderDao.getOrderById(orderId, forUpdate);
    }

    /*check stop orders on order accepted and rates changed*/
    private void checkOrders(ExOrder exOrder, OperationType operationType) {
        try {
            NavigableSet<StopOrderSummaryDto> result;
            switch (operationType) {
                case SELL: {
                    synchronized (getLock(exOrder.getCurrencyPairId(), operationType)) {
                        result = stopOrdersHolder.getSellOrdersForPairAndStopRate(exOrder.getCurrencyPairId(), exOrder.getExRate());
                        log.debug("proc order result {}", result.size());
                        if (!result.isEmpty()) {
                            this.proceedStopOrders(exOrder.getCurrencyPairId(), result);
                        }
                        break;
                    }
                }
                case BUY: {
                    synchronized (getLock(exOrder.getCurrencyPairId(), operationType)) {
                        result = stopOrdersHolder.getBuyOrdersForPairAndStopRate(exOrder.getCurrencyPairId(), exOrder.getExRate());
                        log.debug("buy order result {}", result.size());
                        if (!result.isEmpty()) {
                            this.proceedStopOrders(exOrder.getCurrencyPairId(), result);
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("cant check stop orders {}", e);
        }
    }

    private Object getLock(Integer currencyId, OperationType operationType) {
        switch (operationType) {
            case BUY: {
                buyLocks.putIfAbsent(currencyId, new Object());
                return buyLocks.get(currencyId);
            }
            case SELL: {
                sellLocks.putIfAbsent(currencyId, new Object());
                return sellLocks.get(currencyId);
            }
            default: {
                throw new RuntimeException("Operatione not supported " + operationType);
            }
        }

    }


    /*try to accept order after create*/
    @Override
    public void onStopOrderCreate(ExOrder exOrder) {
        log.debug("stop order created {}", exOrder.getId());
        try {
            BigDecimal currentRate = ratesHolder.getCurrentRate(exOrder.getCurrencyPairId(), exOrder.getOperationType());
            log.debug("current rate {}, stop {}", currentRate, exOrder.getStop() );
            switch (exOrder.getOperationType()) {
                case SELL: {
                    if (currentRate != null && exOrder.getStop().compareTo(currentRate) >= 0) {
                        log.error("try to proceed sell stop order {}", exOrder.getId());
                        this.proceedStopOrder(exOrder);
                    } else {
                        log.error("add buy order to holder {}", exOrder.getId());
                        stopOrdersHolder.addOrder(exOrder);
                    }
                }
                break;
                case BUY: {
                    if (currentRate != null && exOrder.getStop().compareTo(currentRate) <= 0) {
                        log.error("try to proceed buy stop order {}", exOrder.getId());
                        this.proceedStopOrder(exOrder);
                    } else {
                        log.error("add buy order to holder {}", exOrder.getId());
                        stopOrdersHolder.addOrder(exOrder);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            log.error("cant check stop orders on order create {}", e);
        }
    }

    @Override
    public List<OrderWideListDto> getMyOrdersWithState(CacheData cacheData, String email, CurrencyPair currencyPair, OrderStatus status, OperationType operationType, String scope, Integer offset, Integer limit, Locale locale) {
        List<OrderWideListDto> result = stopOrderDao.getMyOrdersWithState(email, currencyPair, status, operationType, scope, offset, limit, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<OrderWideListDto>() {{
                add(new OrderWideListDto(false));
            }};
        }
        return result;
    }

    @Override
    public List<StopOrder> getActiveStopOrdersByCurrencyPairsId(List<Integer> pairIds) {
        return stopOrderDao.getOrdersBypairId(pairIds, OrderStatus.OPENED);
    }

    @PreDestroy
    private void shutdown() {
        checkExecutors.shutdown();
        ordersExecutors.shutdown();
    }
}
