package me.exrates.model.dto;

import java.math.BigDecimal;

public interface NotificatorSubscription {

    String getContactStr();

    BigDecimal getPrice();

    boolean isConnected();
}
