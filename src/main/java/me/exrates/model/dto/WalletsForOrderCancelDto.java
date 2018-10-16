package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class WalletsForOrderCancelDto {
    int orderId;
    int orderStatusId;
    BigDecimal reservedAmount;
    int walletId;
    BigDecimal activeBalance;
    BigDecimal reservedBalance;
}
