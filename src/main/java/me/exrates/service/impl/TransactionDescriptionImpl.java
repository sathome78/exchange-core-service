package me.exrates.service.impl;

import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderStatus;
import me.exrates.service.TransactionDescription;
import org.springframework.stereotype.Service;

@Service
public class TransactionDescriptionImpl implements TransactionDescription {
    @Override
    public String get(OrderStatus status, OrderActionEnum actionEnum) {
        return null;
    }
}
