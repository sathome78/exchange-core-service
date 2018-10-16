package me.exrates.service;

import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderStatus;

public interface TransactionDescription {
    String get(OrderStatus status, OrderActionEnum actionEnum);
}
