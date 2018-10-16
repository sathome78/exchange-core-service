package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import me.exrates.model.enums.NotificatorSubscriptionStateEnum;

import java.math.BigDecimal;

/**
 * Created by Maks on 05.10.2017.
 */
@Data
@Builder
public class TelegramSubscription implements NotificatorSubscription {


    private int id;
    private int userId;
    private String code;
    private NotificatorSubscriptionStateEnum subscriptionState;
    private String userAccount;
    private Long chatId;
    private String rawText;

    @Override
    public boolean isConnected() {
        return subscriptionState.isFinalState();
    }

    @Override
    public String getContactStr() {
        return userAccount;
    }

    @Override
    public BigDecimal getPrice() {
        return null;
    }

    @Tolerate
    public TelegramSubscription() {
    }
}
