package me.exrates.dao;

import me.exrates.model.dto.SmsSubscriptionDto;

import java.math.BigDecimal;

public interface SmsSubscriptionDao {

    int create(SmsSubscriptionDto dto);

    void update(SmsSubscriptionDto dto);

    SmsSubscriptionDto getByUserId(int userId);

    void updateDeliveryPrice(int userId, BigDecimal cost);
}
