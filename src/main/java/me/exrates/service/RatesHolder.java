package me.exrates.service;

import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

public interface RatesHolder {
    BigDecimal getCurrentRate(int currencyPairId, OperationType operationType);
}
