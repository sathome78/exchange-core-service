package me.exrates.model.dto;

import java.math.BigDecimal;

/**
 * Created by Maks on 12.10.2017.
 */
public interface NotificatorSubscription {

    String getContactStr();

    BigDecimal getPrice();

    boolean isConnected();
}
