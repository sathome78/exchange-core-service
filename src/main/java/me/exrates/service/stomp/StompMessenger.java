package me.exrates.service.stomp;

import me.exrates.model.enums.OperationType;

import java.util.List;

public interface StompMessenger {

    void sendRefreshTradeOrdersMessage(Integer pairId, OperationType operationType);

    void sendMyTradesToUser(int userId, Integer currencyPair);

    void sendAllTrades(Integer currencyPair);

    void sendChartData(Integer currencyPairId);

    void sendChartData(Integer currencyPairId, String resolution, String data);

    void sendStatisticMessage(List<Integer> currenciesIds);
}
