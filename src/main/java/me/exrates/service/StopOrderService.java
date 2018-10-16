package me.exrates.service;

import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderInfoDto;
import me.exrates.model.dto.StopOrderSummaryDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.main.CacheData;
import me.exrates.model.main.CurrencyPair;
import me.exrates.model.main.ExOrder;
import me.exrates.model.main.StopOrder;
import me.exrates.model.onlineTableDto.OrderWideListDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.NavigableSet;

public interface StopOrderService {

    @Transactional
    String create(OrderCreateDto orderCreateDto, OrderActionEnum actionEnum, Locale locale);

    Integer createOrder(ExOrder exOrder);

    void proceedStopOrders(int pairId, NavigableSet<StopOrderSummaryDto> orders);

    @Transactional
    void proceedStopOrderAndRemove(int stopOrderId);

    @Transactional
    boolean cancelOrder(ExOrder exOrder, Locale locale);

    @Transactional
    boolean setStatus(int orderId, OrderStatus status);

    OrderCreateDto getOrderById(Integer orderId, boolean forUpdate);

    void onStopOrderCreate(ExOrder exOrder);


    @Transactional(readOnly = true)
    List<OrderWideListDto> getMyOrdersWithState(CacheData cacheData,
                                                String email, CurrencyPair currencyPair, OrderStatus status,
                                                OperationType operationType,
                                                String scope, Integer offset, Integer limit, Locale locale);

    List<StopOrder> getActiveStopOrdersByCurrencyPairsId(List<Integer> pairIds);

}
