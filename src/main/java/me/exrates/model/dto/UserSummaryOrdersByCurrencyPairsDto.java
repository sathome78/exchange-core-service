package me.exrates.model.dto;

import me.exrates.util.BigDecimalProcessing;

import java.math.BigDecimal;

public class UserSummaryOrdersByCurrencyPairsDto {
    private String operationType;
    private String date;
    private String ownerEmail;
    private String ownerNickname;
    private String acceptorEmail;
    private String acceptorNickname;
    private String currencyPair;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;
    private BigDecimal exrate;

    public static String getTitle() {
        return  "operationType" + ";" +
                "date" + ";" +
                "ownerEmail" + ";" +
                "ownerNickname" + ";" +
                "acceptorEmail" + ";" +
                "acceptorNickname" + ";" +
                "currencyPair" + ";" +
                "amountBase" + ";" +
                "amountConvert" + ";" +
                "exrate" +
                "\r\n";
    }

    @Override
    public String toString() {
        return  operationType + ";" +
                date + ";" +
                ownerEmail + ";" +
                ownerNickname + ";" +
                acceptorEmail + ";" +
                acceptorNickname + ";" +
                currencyPair + ";" +
                BigDecimalProcessing.formatNoneComma(amountBase, false) + ";" +
                BigDecimalProcessing.formatNoneComma(amountConvert, false) + ";" +
                BigDecimalProcessing.formatNoneComma(exrate, false) +
                "\r\n";
    }

    /*getters setters*/

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerNickname() {
        return ownerNickname;
    }

    public void setOwnerNickname(String ownerNickname) {
        this.ownerNickname = ownerNickname;
    }

    public String getAcceptorEmail() {
        return acceptorEmail;
    }

    public void setAcceptorEmail(String acceptorEmail) {
        this.acceptorEmail = acceptorEmail;
    }

    public String getAcceptorNickname() {
        return acceptorNickname;
    }

    public void setAcceptorNickname(String acceptorNickname) {
        this.acceptorNickname = acceptorNickname;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(String currencyPair) {
        this.currencyPair = currencyPair;
    }

    public BigDecimal getAmountBase() {
        return amountBase;
    }

    public void setAmountBase(BigDecimal amountBase) {
        this.amountBase = amountBase;
    }

    public BigDecimal getAmountConvert() {
        return amountConvert;
    }

    public void setAmountConvert(BigDecimal amountConvert) {
        this.amountConvert = amountConvert;
    }

    public BigDecimal getExrate() {
        return exrate;
    }

    public void setExrate(BigDecimal exrate) {
        this.exrate = exrate;
    }
}
