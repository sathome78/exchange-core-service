package me.exrates.model.dto;

import lombok.Data;
import me.exrates.model.enums.NotificationPayTypeEnum;

import java.math.BigDecimal;

@Data
public class Notificator {

    private int id;
    private String beanName;
    private BigDecimal messagePrice;
    private BigDecimal subscribePrice;
    private NotificationPayTypeEnum payTypeEnum;
    private boolean enabled;
    private String name;
    private boolean needSubscribe;
}
