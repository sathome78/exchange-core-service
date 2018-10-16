package me.exrates.model.dto;

import me.exrates.model.enums.OperationType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by OLEG on 01.09.2016.
 */
public class OrderCreationParamsDto {
    @NotNull
    private Integer currencyPairId;
    @NotNull
    private OperationType orderType;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private BigDecimal rate;

    public OrderCreationParamsDto() {
    }

    public OrderCreationParamsDto(Integer currencyPairId, OperationType orderType, BigDecimal amount, BigDecimal rate) {
        this.currencyPairId = currencyPairId;
        this.orderType = orderType;
        this.amount = amount;
        this.rate = rate;
    }

    public Integer getCurrencyPairId() {
        return currencyPairId;
    }

    public void setCurrencyPairId(Integer currencyPairId) {
        this.currencyPairId = currencyPairId;
    }

    public OperationType getOrderType() {
        return orderType;
    }

    public void setOrderType(OperationType orderType) {
        this.orderType = orderType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "OrderCreationParamsDto{" +
                "currencyPairId=" + currencyPairId +
                ", orderType=" + orderType +
                ", amount=" + amount +
                ", rate=" + rate +
                '}';
    }
}
