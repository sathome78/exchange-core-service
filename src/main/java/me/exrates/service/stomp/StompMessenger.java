package me.exrates.service.stomp;

import me.exrates.model.enums.OperationType;

import java.util.List;

public interface StompMessenger {
    void sendChartData(Integer currencyPairId, String resolution, String data);
}
