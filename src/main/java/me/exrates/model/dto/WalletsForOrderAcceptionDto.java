package me.exrates.model.dto;

import java.math.BigDecimal;

/**
 * Contains
 * - IDs the currency
 * - IDs the wallets -
 * - balances the wallets of participants the order: user-creator, user-acceptor, company
 * - status the order
 *
 * @author ValkSam
 */
public class WalletsForOrderAcceptionDto {
    int orderId;
    int orderStatusId;
    /**/
    int currencyBase;
    int currencyConvert;
    /**/
    int companyWalletCurrencyBase;
    BigDecimal companyWalletCurrencyBaseBalance;
    BigDecimal companyWalletCurrencyBaseCommissionBalance;
    /**/
    int companyWalletCurrencyConvert;
    BigDecimal companyWalletCurrencyConvertBalance;
    BigDecimal companyWalletCurrencyConvertCommissionBalance;
    /**/
    int userCreatorInWalletId;
    BigDecimal userCreatorInWalletActiveBalance;
    BigDecimal userCreatorInWalletReservedBalance;
    /**/
    int userCreatorOutWalletId;
    BigDecimal userCreatorOutWalletActiveBalance;
    BigDecimal userCreatorOutWalletReservedBalance;
    /**/
    int userAcceptorInWalletId;
    BigDecimal userAcceptorInWalletActiveBalance;
    BigDecimal userAcceptorInWalletReservedBalance;
    /**/
    int userAcceptorOutWalletId;
    BigDecimal userAcceptorOutWalletActiveBalance;
    BigDecimal userAcceptorOutWalletReservedBalance;

    /*getters setters*/

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(int orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public int getCurrencyBase() {
        return currencyBase;
    }

    public void setCurrencyBase(int currencyBase) {
        this.currencyBase = currencyBase;
    }

    public int getCurrencyConvert() {
        return currencyConvert;
    }

    public void setCurrencyConvert(int currencyConvert) {
        this.currencyConvert = currencyConvert;
    }

    public int getCompanyWalletCurrencyBase() {
        return companyWalletCurrencyBase;
    }

    public void setCompanyWalletCurrencyBase(int companyWalletCurrencyBase) {
        this.companyWalletCurrencyBase = companyWalletCurrencyBase;
    }

    public BigDecimal getCompanyWalletCurrencyBaseBalance() {
        return companyWalletCurrencyBaseBalance;
    }

    public void setCompanyWalletCurrencyBaseBalance(BigDecimal companyWalletCurrencyBaseBalance) {
        this.companyWalletCurrencyBaseBalance = companyWalletCurrencyBaseBalance;
    }

    public BigDecimal getCompanyWalletCurrencyBaseCommissionBalance() {
        return companyWalletCurrencyBaseCommissionBalance;
    }

    public void setCompanyWalletCurrencyBaseCommissionBalance(BigDecimal companyWalletCurrencyBaseCommissionBalance) {
        this.companyWalletCurrencyBaseCommissionBalance = companyWalletCurrencyBaseCommissionBalance;
    }

    public int getCompanyWalletCurrencyConvert() {
        return companyWalletCurrencyConvert;
    }

    public void setCompanyWalletCurrencyConvert(int companyWalletCurrencyConvert) {
        this.companyWalletCurrencyConvert = companyWalletCurrencyConvert;
    }

    public BigDecimal getCompanyWalletCurrencyConvertBalance() {
        return companyWalletCurrencyConvertBalance;
    }

    public void setCompanyWalletCurrencyConvertBalance(BigDecimal companyWalletCurrencyConvertBalance) {
        this.companyWalletCurrencyConvertBalance = companyWalletCurrencyConvertBalance;
    }

    public BigDecimal getCompanyWalletCurrencyConvertCommissionBalance() {
        return companyWalletCurrencyConvertCommissionBalance;
    }

    public void setCompanyWalletCurrencyConvertCommissionBalance(BigDecimal companyWalletCurrencyConvertCommissionBalance) {
        this.companyWalletCurrencyConvertCommissionBalance = companyWalletCurrencyConvertCommissionBalance;
    }

    public int getUserCreatorInWalletId() {
        return userCreatorInWalletId;
    }

    public void setUserCreatorInWalletId(int userCreatorInWalletId) {
        this.userCreatorInWalletId = userCreatorInWalletId;
    }

    public BigDecimal getUserCreatorInWalletActiveBalance() {
        return userCreatorInWalletActiveBalance;
    }

    public void setUserCreatorInWalletActiveBalance(BigDecimal userCreatorInWalletActiveBalance) {
        this.userCreatorInWalletActiveBalance = userCreatorInWalletActiveBalance;
    }

    public BigDecimal getUserCreatorInWalletReservedBalance() {
        return userCreatorInWalletReservedBalance;
    }

    public void setUserCreatorInWalletReservedBalance(BigDecimal userCreatorInWalletReservedBalance) {
        this.userCreatorInWalletReservedBalance = userCreatorInWalletReservedBalance;
    }

    public int getUserCreatorOutWalletId() {
        return userCreatorOutWalletId;
    }

    public void setUserCreatorOutWalletId(int userCreatorOutWalletId) {
        this.userCreatorOutWalletId = userCreatorOutWalletId;
    }

    public BigDecimal getUserCreatorOutWalletActiveBalance() {
        return userCreatorOutWalletActiveBalance;
    }

    public void setUserCreatorOutWalletActiveBalance(BigDecimal userCreatorOutWalletActiveBalance) {
        this.userCreatorOutWalletActiveBalance = userCreatorOutWalletActiveBalance;
    }

    public BigDecimal getUserCreatorOutWalletReservedBalance() {
        return userCreatorOutWalletReservedBalance;
    }

    public void setUserCreatorOutWalletReservedBalance(BigDecimal userCreatorOutWalletReservedBalance) {
        this.userCreatorOutWalletReservedBalance = userCreatorOutWalletReservedBalance;
    }

    public int getUserAcceptorInWalletId() {
        return userAcceptorInWalletId;
    }

    public void setUserAcceptorInWalletId(int userAcceptorInWalletId) {
        this.userAcceptorInWalletId = userAcceptorInWalletId;
    }

    public BigDecimal getUserAcceptorInWalletActiveBalance() {
        return userAcceptorInWalletActiveBalance;
    }

    public void setUserAcceptorInWalletActiveBalance(BigDecimal userAcceptorInWalletActiveBalance) {
        this.userAcceptorInWalletActiveBalance = userAcceptorInWalletActiveBalance;
    }

    public BigDecimal getUserAcceptorInWalletReservedBalance() {
        return userAcceptorInWalletReservedBalance;
    }

    public void setUserAcceptorInWalletReservedBalance(BigDecimal userAcceptorInWalletReservedBalance) {
        this.userAcceptorInWalletReservedBalance = userAcceptorInWalletReservedBalance;
    }

    public int getUserAcceptorOutWalletId() {
        return userAcceptorOutWalletId;
    }

    public void setUserAcceptorOutWalletId(int userAcceptorOutWalletId) {
        this.userAcceptorOutWalletId = userAcceptorOutWalletId;
    }

    public BigDecimal getUserAcceptorOutWalletActiveBalance() {
        return userAcceptorOutWalletActiveBalance;
    }

    public void setUserAcceptorOutWalletActiveBalance(BigDecimal userAcceptorOutWalletActiveBalance) {
        this.userAcceptorOutWalletActiveBalance = userAcceptorOutWalletActiveBalance;
    }

    public BigDecimal getUserAcceptorOutWalletReservedBalance() {
        return userAcceptorOutWalletReservedBalance;
    }

    public void setUserAcceptorOutWalletReservedBalance(BigDecimal userAcceptorOutWalletReservedBalance) {
        this.userAcceptorOutWalletReservedBalance = userAcceptorOutWalletReservedBalance;
    }
}
