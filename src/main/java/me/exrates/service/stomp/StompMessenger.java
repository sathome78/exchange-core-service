package me.exrates.service.stomp;

public interface StompMessenger {
    void sendChartData(Integer currencyPairId, String resolution, String data);
}
