package me.exrates.model.dto;

import me.exrates.util.BigDecimalProcessing;

import java.math.BigDecimal;

public class UserSummaryOrdersDto {
    private String userEmail;
    private String wallet;
    private String role;
    private BigDecimal amountBuy;
    private BigDecimal amountBuyFee;
    private BigDecimal amountSell;
    private BigDecimal amountSellFee;

    public static String getTitle() {
        return "email" + ";" +
                "wallet" + ";" +
                "group" + ";" +
                "buy" + ";" +
                "buyFee" + ";" +
                "sell" + ";" +
                "sellFee" +
                "\r\n";
    }

    @Override
    public String toString() {
        return userEmail + ";" +
                wallet + ";" +
                role + ";" +
                BigDecimalProcessing.formatNoneComma(amountBuy, false) + ";" +
                BigDecimalProcessing.formatNoneComma(amountBuyFee, false) + ";" +
                BigDecimalProcessing.formatNoneComma(amountSell, false) + ";" +
                BigDecimalProcessing.formatNoneComma(amountSellFee, false) +
                "\r\n";
    }

    /*getters setters*/

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public BigDecimal getAmountBuy() {
        return amountBuy;
    }

    public void setAmountBuy(BigDecimal amountBuy) {
        this.amountBuy = amountBuy;
    }

    public BigDecimal getAmountSell() {
        return amountSell;
    }

    public void setAmountSell(BigDecimal amountSell) {
        this.amountSell = amountSell;
    }

    public BigDecimal getAmountBuyFee() {
        return amountBuyFee;
    }

    public void setAmountBuyFee(BigDecimal amountBuyFee) {
        this.amountBuyFee = amountBuyFee;
    }

    public BigDecimal getAmountSellFee() {
        return amountSellFee;
    }

    public void setAmountSellFee(BigDecimal amountSellFee) {
        this.amountSellFee = amountSellFee;
    }
}
