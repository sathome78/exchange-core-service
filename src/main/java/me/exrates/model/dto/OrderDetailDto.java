package me.exrates.model.dto;

import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;

import java.math.BigDecimal;

public class OrderDetailDto {
    private int orderId;
    private OrderStatus orderStatus;
    private BigDecimal orderCreatorReservedAmount;
    private int orderCreatorReservedWalletId;
    private int transactionId;
    private OperationType transactionType;
    private BigDecimal transactionAmount;
    private int userWalletId;
    private int companyWalletId;
    private BigDecimal companyCommission;

    public OrderDetailDto(int orderId, int orderStatusId, BigDecimal orderCreatorReservedAmount, int orderCreatorReservedWalletId, int transactionId, int transactionTypeId, BigDecimal transactionAmount, int userWalletId, int companyWalletId, BigDecimal companyCommission) {
        this.orderId = orderId;
        this.orderStatus = OrderStatus.convert(orderStatusId);
        this.orderCreatorReservedAmount = orderCreatorReservedAmount;
        this.orderCreatorReservedWalletId = orderCreatorReservedWalletId;
        this.transactionId = transactionId;
        if (transactionTypeId != 0) {
            this.transactionType = OperationType.convert(transactionTypeId);
        }
        this.transactionAmount = transactionAmount;
        this.userWalletId = userWalletId;
        this.companyWalletId = companyWalletId;
        this.companyCommission = companyCommission;
    }

    /*getters setters*/

    public int getOrderId() {
        return orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public BigDecimal getOrderCreatorReservedAmount() {
        return orderCreatorReservedAmount;
    }

    public int getOrderCreatorReservedWalletId() {
        return orderCreatorReservedWalletId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public OperationType getTransactionType() {
        return transactionType;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public int getUserWalletId() {
        return userWalletId;
    }

    public int getCompanyWalletId() {
        return companyWalletId;
    }

    public BigDecimal getCompanyCommission() {
        return companyCommission;
    }
};

