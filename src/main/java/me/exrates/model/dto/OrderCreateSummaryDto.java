package me.exrates.model.dto;

import me.exrates.util.BigDecimalProcessing;

import java.util.Locale;

public class OrderCreateSummaryDto {
    private String currencyPairName;
    private String operationTypeName;
    private String balance;
    private String amount;
    private String exrate;
    private String total;
    private String commission;
    private String totalWithComission;
    private String stop;
    private String baseType;

    public OrderCreateSummaryDto() {
    }

    public OrderCreateSummaryDto(OrderCreateDto orderCreateDto, Locale locale) {
        this.currencyPairName = orderCreateDto.getCurrencyPair().getName();
        this.operationTypeName = orderCreateDto.getOperationType().name();
        this.balance = BigDecimalProcessing.formatLocale(orderCreateDto.getSpentWalletBalance(), locale, 2);
        this.amount = BigDecimalProcessing.formatLocale(orderCreateDto.getAmount(), locale, 2);
        this.exrate = BigDecimalProcessing.formatLocale(orderCreateDto.getExchangeRate(), locale, 2);
        this.total = BigDecimalProcessing.formatLocale(orderCreateDto.getTotal(), locale, 2);
        this.commission = BigDecimalProcessing.formatLocale(orderCreateDto.getComission(), locale, 2);
        this.totalWithComission = BigDecimalProcessing.formatLocale(orderCreateDto.getTotalWithComission(), locale, 2);
        this.stop = BigDecimalProcessing.formatLocale(orderCreateDto.getStop(), locale, 2);
        this.baseType = orderCreateDto.getOrderBaseType().name();
    }

    /*getters setters*/

    public String getCurrencyPairName() {
        return currencyPairName;
    }

    public void setCurrencyPairName(String currencyPairName) {
        this.currencyPairName = currencyPairName;
    }

    public String getOperationTypeName() {
        return operationTypeName;
    }

    public void setOperationTypeName(String operationTypeName) {
        this.operationTypeName = operationTypeName;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getExrate() {
        return exrate;
    }

    public void setExrate(String exrate) {
        this.exrate = exrate;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getTotalWithComission() {
        return totalWithComission;
    }

    public void setTotalWithComission(String totalWithComission) {
        this.totalWithComission = totalWithComission;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    @Override
    public String toString() {
        return "OrderCreateSummaryDto{" +
                "currencyPairName='" + currencyPairName + '\'' +
                ", operationTypeName='" + operationTypeName + '\'' +
                ", balance='" + balance + '\'' +
                ", amount='" + amount + '\'' +
                ", exrate='" + exrate + '\'' +
                ", total='" + total + '\'' +
                ", commission='" + commission + '\'' +
                ", totalWithComission='" + totalWithComission + '\'' +
                '}';
    }
}
