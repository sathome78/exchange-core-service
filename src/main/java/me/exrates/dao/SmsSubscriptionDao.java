package me.exrates.dao;

import me.exrates.model.dto.SmsSubscriptionDto;

import java.math.BigDecimal;

/**
 * Created by Maks on 09.10.2017.
 */
public interface SmsSubscriptionDao {

    int create(SmsSubscriptionDto dto);

    void update(SmsSubscriptionDto dto);

    SmsSubscriptionDto getByUserId(int userId);

    void updateDeliveryPrice(int userId, BigDecimal cost);
}
