package me.exrates.service;

import me.exrates.model.dto.StopOrderSummaryDto;
import me.exrates.model.main.ExOrder;

import java.math.BigDecimal;
import java.util.NavigableSet;

public interface StopOrdersHolder {
    void addOrder(ExOrder exOrder);

    NavigableSet<StopOrderSummaryDto> getBuyOrdersForPairAndStopRate(int currencyPairId, BigDecimal exRate);

    NavigableSet<StopOrderSummaryDto> getSellOrdersForPairAndStopRate(int currencyPairId, BigDecimal exRate);

    void delete(int currencyPairId, StopOrderSummaryDto stopOrderSummaryDto);

}
