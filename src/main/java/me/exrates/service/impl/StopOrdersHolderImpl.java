package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.StopOrderSummaryDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.main.CurrencyPair;
import me.exrates.model.main.ExOrder;
import me.exrates.model.main.StopOrder;
import me.exrates.service.CurrencyService;
import me.exrates.service.StopOrderService;
import me.exrates.service.StopOrdersHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@Service
@Log4j2
public class StopOrdersHolderImpl implements StopOrdersHolder {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private StopOrderService stopOrderService;

    /*contains: integer - currency pair id; ConcurrentSkipListSet - set with orders for this pair*/
    private Map<Integer, ConcurrentSkipListSet<StopOrderSummaryDto>> sellOrdersMap = new ConcurrentHashMap<>();
    private Map<Integer, ConcurrentSkipListSet<StopOrderSummaryDto>> buyOrdersMap = new ConcurrentHashMap<>();

    private Comparator<StopOrderSummaryDto> comparator = Comparator.comparing(StopOrderSummaryDto::getStopRate)
            .thenComparing(StopOrderSummaryDto::getOrderId);


    /*----methods-----*/
    @PostConstruct
    public void init() {
        List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs(CurrencyPairType.MAIN);
        List<StopOrder> activeOrders = stopOrderService
                .getActiveStopOrdersByCurrencyPairsId(currencyPairs.stream().map(CurrencyPair::getId).collect(Collectors.toList()));
        currencyPairs.forEach(p -> {
            List<StopOrder> thisPairsOrders = activeOrders.stream().filter(i -> i.getCurrencyPairId() == p.getId()).collect(Collectors.toList());
            ConcurrentSkipListSet<StopOrderSummaryDto> sellSet = new ConcurrentSkipListSet<>(comparator);
            thisPairsOrders.stream().filter(i -> i.getOperationType().equals(OperationType.SELL)).forEach(i -> {
                sellSet.add(new StopOrderSummaryDto(i.getId(), i.getStop(), i.getOperationType()));
            });
            sellOrdersMap.put(p.getId(), sellSet);
            log.debug("sell set for currency {} size: {}", p.getId(), sellSet.size());
            ConcurrentSkipListSet<StopOrderSummaryDto> buySet = new ConcurrentSkipListSet<>(comparator);
            thisPairsOrders.stream().filter(i -> i.getOperationType().equals(OperationType.BUY)).forEach(i -> {
                buySet.add(new StopOrderSummaryDto(i.getId(), i.getStop(), i.getOperationType()));
            });
            buyOrdersMap.put(p.getId(), buySet);
            log.debug("buy set for currency {} size: {}", p.getId(), buySet.size());
        });
    }

    /**
     * return set with orders by this currency pair @pairId pair which has higher or equal @rate
     */
    @Override
    public NavigableSet<StopOrderSummaryDto> getSellOrdersForPairAndStopRate(int pairId, BigDecimal rate) {
        if (!sellOrdersMap.containsKey(pairId)) {
            addNewPairToMap(sellOrdersMap, pairId);
            return Collections.emptyNavigableSet();
        }
        log.debug("order accepted {} {}", pairId, rate);
        ConcurrentSkipListSet<StopOrderSummaryDto> thisOrdersSet = sellOrdersMap.get(pairId);
        log.debug("sell orders size {}", thisOrdersSet.size());
        return thisOrdersSet.tailSet(new StopOrderSummaryDto(0, rate), true);
    }

    /**
     * return set with orders by this currency pair @pairId pair which has lower or equal @rate
     */
    @Override
    public NavigableSet<StopOrderSummaryDto> getBuyOrdersForPairAndStopRate(int pairId, BigDecimal rate) {
        if (!buyOrdersMap.containsKey(pairId)) {
            addNewPairToMap(buyOrdersMap, pairId);
            return Collections.emptyNavigableSet();
        }
        log.debug("order accepted {} {}", pairId, rate);
        ConcurrentSkipListSet<StopOrderSummaryDto> thisOrdersSet = buyOrdersMap.get(pairId);
        log.debug("buy orders size {}", thisOrdersSet.size());
        return thisOrdersSet.headSet(new StopOrderSummaryDto(Integer.MAX_VALUE, rate), true);
    }

    @Override
    public void delete(int pairId, StopOrderSummaryDto summaryDto) {
        ConcurrentSkipListSet<StopOrderSummaryDto> thisOrdersSet;
        switch (summaryDto.getOperationType()) {
            case BUY: {
                thisOrdersSet = buyOrdersMap.get(pairId);
                break;
            }
            case SELL: {
                thisOrdersSet = sellOrdersMap.get(pairId);
                break;
            }
            default: {
                throw new RuntimeException("wrong order operation type! ".concat(summaryDto.toString()));
            }
        }
        if (!thisOrdersSet.contains(summaryDto)) {
            ;
            throw new RuntimeException("map not conatins this order! ".concat(summaryDto.toString()));
        }
        log.debug("delete, before: {}", thisOrdersSet.size());
        thisOrdersSet.remove(summaryDto);
        log.debug("delete, after: {}", thisOrdersSet.size());
    }

    @Override
    public void addOrder(ExOrder exOrder) {
        log.debug("add order: {}", exOrder.getId());
        switch (exOrder.getOperationType()) {
            case BUY: {
                log.error("add buy before: {}", buyOrdersMap.get(exOrder.getCurrencyPairId()).size());
                buyOrdersMap.get(exOrder.getCurrencyPairId())
                        .add(new StopOrderSummaryDto(exOrder.getId(), exOrder.getStop(), exOrder.getOperationType()));
                log.error("add buy after: {}", buyOrdersMap.get(exOrder.getCurrencyPairId()).size());
                break;
            }
            case SELL: {
                log.error("add sell before: {}", buyOrdersMap.get(exOrder.getCurrencyPairId()).size());
                sellOrdersMap.get(exOrder.getCurrencyPairId())
                        .add(new StopOrderSummaryDto(exOrder.getId(), exOrder.getStop(), exOrder.getOperationType()));
                log.error("add sell after: {}", buyOrdersMap.get(exOrder.getCurrencyPairId()).size());
                break;
            }
        }
    }

    private void addNewPairToMap(Map<Integer, ConcurrentSkipListSet<StopOrderSummaryDto>> map, Integer currencyPairId) {
        map.put(currencyPairId, new ConcurrentSkipListSet<>(comparator));
    }
}
