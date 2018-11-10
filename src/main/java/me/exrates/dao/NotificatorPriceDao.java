package me.exrates.dao;


import me.exrates.model.dto.NotificatorTotalPriceDto;

import java.math.BigDecimal;

public interface NotificatorPriceDao {
    BigDecimal getFeeMessagePrice(int notificatorId, int roleId);

    NotificatorTotalPriceDto getPrices(int notificatorId, int roleId);

    BigDecimal getSubscriptionPrice(int notificatorId, int roleId);
}
