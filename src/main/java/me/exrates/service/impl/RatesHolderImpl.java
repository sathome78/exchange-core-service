package me.exrates.service.impl;

import me.exrates.model.enums.OperationType;
import me.exrates.service.RatesHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RatesHolderImpl implements RatesHolder {
    @Override
    public BigDecimal getCurrentRate(int currencyPairId, OperationType operationType) {
        return null;
    }
}
