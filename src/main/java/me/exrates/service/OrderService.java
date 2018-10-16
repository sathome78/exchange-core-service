package me.exrates.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.exrates.model.chart.ChartResolution;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.*;
import me.exrates.model.enums.*;
import me.exrates.model.main.*;
import me.exrates.model.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.onlineTableDto.OrderListDto;
import me.exrates.model.onlineTableDto.OrderWideListDto;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;


public interface OrderService {

    String getAllCurrenciesStatForRefresh(RefreshObjectsEnum refreshObjectsEnum);

    String getAllAndMyTradesForInit(int pairId, Principal principal) throws JsonProcessingException;

    OrderCreateDto prepareNewOrder(CurrencyPair activeCurrencyPair, OperationType orderType, String userEmail, BigDecimal amount, BigDecimal rate, OrderBaseType baseType);

    OrderCreateDto prepareNewOrder(CurrencyPair activeCurrencyPair, OperationType orderType, String userEmail, BigDecimal amount, BigDecimal rate, Integer sourceId, OrderBaseType baseType);

    OrderValidationDto validateOrder(OrderCreateDto orderCreateDto);

    @Transactional
    String createOrder(OrderCreateDto orderCreateDto, OrderActionEnum action, Locale locale);

    @Transactional
    Integer createOrderByStopOrder(OrderCreateDto orderCreateDto, OrderActionEnum action, Locale locale);

    int createOrder(OrderCreateDto order, OrderActionEnum action);

    Optional<String> autoAccept(OrderCreateDto orderCreateDto, Locale locale);

    Optional<OrderCreationResultDto> autoAcceptOrders(OrderCreateDto orderCreateDto, Locale locale);

    public OrderCreateDto getMyOrderById(int orderId);

    ExOrder getOrderById(int orderId);

    boolean setStatus(int orderId, OrderStatus status);

    void acceptOrdersList(int userAcceptorId, List<Integer> ordersList, Locale locale);
    boolean cancelOrder(ExOrder exOrder, Locale locale);

    boolean updateOrder(ExOrder exOrder);

    List<CoinmarketApiDto> getCoinmarketDataForActivePairs(String currencyPairName, BackDealInterval backDealInterval);

    List<CoinmarketApiDto> getDailyCoinmarketData(String currencyPairName);

    OrderInfoDto getOrderInfo(int orderId, Locale locale);

    Object deleteOrderForPartialAccept(int orderId);

    List<OrderWideListDto> getMyOrdersWithState(
            CacheData cacheData,
            String email, CurrencyPair currencyPair, OrderStatus status,
            OperationType operationType,
            String scope, Integer offset, Integer limit, Locale locale);

    List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval interval);

    List<CandleChartItemDto> getDataForCandleChart(int pairId, ChartTimeFrame timeFrame);

    WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                    OperationType operationType);

    Iterable<BackDealInterval> getIntervals();

    List<ChartTimeFrame> getChartTimeFrames();

    Map<RefreshObjectsEnum, String> getSomeCurrencyStatForRefresh(List<Integer> currenciesIds);

    String getOrdersForRefresh(Integer pairId, OperationType operationType, UserRole userRole);

    List<OrderListDto> getAllBuyOrdersEx(CurrencyPair currencyPair, Locale locale, UserRole userRole);

    List<OrderListDto> getAllSellOrdersEx(CurrencyPair currencyPair, Locale locale, UserRole userRole);

    List<ExOrderStatisticsShortByPairsDto> getStatForSomeCurrencies(List<Integer> pairsIds);

    String getChartData(Integer currencyPairId, BackDealInterval p);

    String getTradesForRefresh(Integer pairId, String email, RefreshObjectsEnum refreshObjectEnum);

    List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriodEx(String email,
                                                              BackDealInterval backDealInterval,
                                                              Integer limit, CurrencyPair currencyPair, Locale locale);

    List<CandleChartItemDto> getLastDataForCandleChart(Integer currencyPairId,
                                                       LocalDateTime startTime, ChartResolution resolution);

    @Transactional(readOnly = true)
    List<ExOrderStatisticsShortByPairsDto> getOrdersStatisticByPairsEx(RefreshObjectsEnum refreshObjectsEnum);

    String getAllCurrenciesStatForRefreshForAllPairs();

    List<Map<String, Object>> getDataForAreaChart(CurrencyPair currencyPair, BackDealInterval backDealInterval);


    ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval, Locale resolveLocale);

    List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(CacheData cacheData, String email, BackDealInterval orderHistoryInterval, Integer orderHistoryLimit, CurrencyPair currencyPair, Locale resolveLocale);

    OrderCommissionsDto getCommissionForOrder();

    List<OrderListDto> getAllSellOrders(CacheData cacheData, CurrencyPair currencyPair, Locale resolveLocale, Boolean orderRoleFilterEnabled);

    List<OrderListDto> getAllBuyOrders(CacheData cacheData, CurrencyPair currencyPair, Locale resolveLocale, Boolean orderRoleFilterEnabled);

    List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, OrderStatus cancelled, OperationType o, String scope, int i, int i1, Locale locale);

    List<ExOrderStatisticsShortByPairsDto> getOrdersStatisticByPairs(CacheData cacheData, Locale resolveLocale);
}
