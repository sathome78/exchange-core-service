package me.exrates.model.enums;

import me.exrates.exception.UnsupportedOrderStatusException;

import java.util.stream.Stream;

public enum OrderStatus {

    INPROCESS(1),
    OPENED(2),
    CLOSED(3),
    CANCELLED(4),
    DELETED(5),
    DRAFT(6),
    SPLIT_CLOSED(7);

    private final int status;

    OrderStatus(int status) {
        this.status = status;
    }

    public static OrderStatus convert(int id) {
        return Stream.of(OrderStatus.values()).filter(item -> item.getStatus() == id)
                .findAny().orElseThrow(() -> new UnsupportedOrderStatusException(id));
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
