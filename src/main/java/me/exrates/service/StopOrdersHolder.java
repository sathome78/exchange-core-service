package me.exrates.service;

import me.exrates.model.dto.StopOrderSummaryDto;
import me.exrates.model.main.ExOrder;

public interface StopOrdersHolder {
    void addOrder(ExOrder exOrder);

    void delete(int currencyPairId, StopOrderSummaryDto stopOrderSummaryDto);

}
