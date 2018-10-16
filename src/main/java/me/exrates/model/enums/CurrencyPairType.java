package me.exrates.model.enums;

import static me.exrates.model.enums.OrderBaseType.LIMIT;

public enum CurrencyPairType {

    MAIN(LIMIT), ICO(me.exrates.model.enums.OrderBaseType.ICO), ALL(null);

    private OrderBaseType orderBaseType;

    public OrderBaseType getOrderBaseType() {
        return orderBaseType;
    }

    CurrencyPairType(OrderBaseType orderBaseType) {
        this.orderBaseType = orderBaseType;
    }
}
