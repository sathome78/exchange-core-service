package me.exrates.dao;

import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.main.CurrencyPair;
import me.exrates.model.main.StopOrder;
import me.exrates.model.onlineTableDto.OrderWideListDto;

import java.util.List;
import java.util.Locale;

public interface StopOrderDao {

    boolean setStatus(int orderId, OrderStatus status);

    Integer create(StopOrder order);

    boolean setStatusAndChildOrderId(int orderId, Integer childOrderId, OrderStatus status);

    OrderCreateDto getOrderById(Integer orderId, boolean forUpdate);

    List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, OrderStatus status, OperationType operationType, String scope, Integer offset, Integer limit, Locale locale);

    List<StopOrder> getOrdersBypairId(List<Integer> pairIds, OrderStatus opened);

    boolean updateOrder(int orderId, StopOrder order);
}
