package me.exrates.model.enums;

import me.exrates.exception.UnsupportedOrderTypeException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by OLEG on 06.04.2017.
 */
public enum OrderType {
    SELL(1, OperationType.SELL, Comparator.naturalOrder()),

    BUY(2, OperationType.BUY, Comparator.reverseOrder());

    private int type;
    private OperationType operationType;
    // needed for sorting orders by rate: DESC for BUY, ASC for SELL
    private Comparator<BigDecimal> benefitRateComparator;

    public int getType() {
        return type;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public Comparator<BigDecimal> getBenefitRateComparator() {
        return benefitRateComparator;
    }

    OrderType(int type, OperationType operationType, Comparator<BigDecimal> benefitRateComparator) {
        this.type = type;
        this.operationType = operationType;
        this.benefitRateComparator = benefitRateComparator;
    }

    public static OrderType convert(int type) {
        return Arrays.stream(OrderType.values()).filter(ot -> ot.type == type).findAny()
                .orElseThrow(UnsupportedOrderTypeException::new);
    }

    public static OrderType convert(String name) {
        return Arrays.stream(OrderType.values()).filter(ot -> ot.name().equals(name)).findAny()
                .orElseThrow(UnsupportedOrderTypeException::new);
    }

    public static OrderType fromOperationType(OperationType operationType) {
        return Arrays.stream(OrderType.values())
                .filter(item -> item.operationType == operationType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Operation type %s not convertible to order type", operationType.name())));
    }

    @Override
    public String toString() {
        return this.name();
    }


}
