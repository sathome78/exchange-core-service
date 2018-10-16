package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class ExternalWalletsDto {

    private Integer currencyId;
    private Integer merchantId;
    private String currencyName;
    private BigDecimal reservedWalletBalance;
    private BigDecimal coldWalletBalance;
    private BigDecimal mainWalletBalance = BigDecimal.ZERO;
    private BigDecimal mainWalletBalanceUSD = BigDecimal.ZERO;
    private BigDecimal totalReal;
    private BigDecimal rateUsdAdditional;

    private BigDecimal totalWalletsBalance;
    private BigDecimal totalWalletsBalanceUSD;
    private BigDecimal totalWalletsDifference;
    private BigDecimal totalWalletsDifferenceUSD;

}
