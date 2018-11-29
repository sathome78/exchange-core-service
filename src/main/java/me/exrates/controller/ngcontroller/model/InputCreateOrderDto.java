package me.exrates.controller.ngcontroller.model;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class InputCreateOrderDto {

    @NotNull
    private String orderType;

    private Integer orderId;

    @NotNull
    private Integer currencyPairId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private BigDecimal rate;

    @NotNull
    private BigDecimal commission;

    private String baseType;

    @NotNull
    private BigDecimal total;

    private BigDecimal stop;

    private String status;

    public InputCreateOrderDto() {
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public BigDecimal getStop() {
        return stop;
    }

    public void setStop(BigDecimal stop) {
        this.stop = stop;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Integer getCurrencyPairId() {
        return currencyPairId;
    }

    public void setCurrencyPairId(Integer currencyPairId) {
        this.currencyPairId = currencyPairId;
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

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("InputCreateOrderDto{");
        sb.append("currencyPairId=").append(currencyPairId);
        sb.append(", amount=").append(amount);
        sb.append(", rate=").append(rate);
        sb.append(", commission=").append(commission);
        sb.append(", baseType='").append(baseType).append('\'');
        sb.append(", total=").append(total);
        sb.append(", stop=").append(stop);
        sb.append('}');
        return sb.toString();
    }
}
