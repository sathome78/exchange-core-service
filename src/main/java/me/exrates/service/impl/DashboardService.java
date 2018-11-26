package me.exrates.service.impl;

import java.math.BigDecimal;

public interface DashboardService {
    BigDecimal getBalanceByCurrency(int userId, int currencyId);
}
