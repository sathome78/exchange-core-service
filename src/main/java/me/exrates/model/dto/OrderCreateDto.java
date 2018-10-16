package me.exrates.model.dto;

import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.main.CurrencyPair;

import java.math.BigDecimal;

import static me.exrates.util.BigDecimalProcessing.doAction;
import static me.exrates.util.BigDecimalProcessing.normalize;


public class OrderCreateDto {
    /*this field filled from existing order*/
    private int orderId;
    private int userId;
    private OrderStatus status;
    /*these fields will be transferred to blank creation form */
    private CurrencyPair currencyPair;
    private int comissionForBuyId;
    private BigDecimal comissionForBuyRate;
    private int comissionForSellId;
    private BigDecimal comissionForSellRate;
    private int walletIdCurrencyBase;
    private BigDecimal currencyBaseBalance;
    private int walletIdCurrencyConvert;
    private BigDecimal currencyConvertBalance;
    //
    /*these fields will be returned from creation form after submitting*/
    /*IMPORTANT: operationType is not populated because OrderCreateDto is used for page the orders,
that consists two forms: for BUY and for SELL. After submit this field will be set because we submit concrete form: BUY or SELL.
However if we transfered to form the orders from dashboard, the fields one form (of two forms: SELL or BUY) must be filled.
To determine which of these forms to be filled, we must set field operationType
*/
    private BigDecimal stop; //stop rate for stop order
    private OperationType operationType;
    private BigDecimal exchangeRate;
    private BigDecimal amount; //amount of base currency: base currency can be bought or sold dependending on operationType
    private OrderBaseType orderBaseType;
    //
    /*
     * these fields will be calculated after submitting the order and before final creation confirmation the order
     * (here: OrderController.submitNewOrderToSell())
     * These amounts calculated directly in java (after check the order parameters in java validator) and will be persistented in db
     * (before this step these amounts were being calculated by javascript and may be occur some difference)
     * */
    private BigDecimal spentWalletBalance;
    private BigDecimal spentAmount;
    private BigDecimal total; //calculated amount of currency conversion = amount * exchangeRate
    private int comissionId;
    private BigDecimal comission; //calculated comission amount depending on operationType and corresponding comission rate
    private BigDecimal totalWithComission; //total + comission
    private Integer sourceId;

    /*constructors*/

    public OrderCreateDto() {
    }

    /*service methods*/
    public OrderCreateDto calculateAmounts() {
        if (operationType == null) {
            return this;
        }
        if (operationType == me.exrates.model.enums.OperationType.SELL) {
            this.spentWalletBalance = this.currencyBaseBalance == null ? BigDecimal.ZERO : this.currencyBaseBalance;
            this.total = doAction(this.amount, this.exchangeRate, ActionType.MULTIPLY);
            this.comissionId = this.comissionForSellId;
            this.comission = doAction(this.total, this.comissionForSellRate, ActionType.MULTIPLY_PERCENT);
            this.totalWithComission = doAction(this.total, this.comission.negate(), ActionType.ADD);
            this.spentAmount = this.amount;
        } else if (operationType == OperationType.BUY) {
            this.spentWalletBalance = this.currencyConvertBalance == null ? BigDecimal.ZERO : this.currencyConvertBalance;
            this.total = doAction(this.amount, this.exchangeRate, ActionType.MULTIPLY);
            this.comissionId = this.comissionForBuyId;
            this.comission = doAction(this.total, this.comissionForBuyRate, ActionType.MULTIPLY_PERCENT);
            this.totalWithComission = doAction(this.total, this.comission, ActionType.ADD);
            this.spentAmount = doAction(this.total, this.comission, ActionType.ADD);
        }
        return this;
    }

    @Override
    public String toString() {
        return "OrderCreateDto{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", status=" + status +
                ", currencyPair=" + currencyPair +
                ", comissionForBuyId=" + comissionForBuyId +
                ", comissionForBuyRate=" + comissionForBuyRate +
                ", comissionForSellId=" + comissionForSellId +
                ", comissionForSellRate=" + comissionForSellRate +
                ", walletIdCurrencyBase=" + walletIdCurrencyBase +
                ", currencyBaseBalance=" + currencyBaseBalance +
                ", walletIdCurrencyConvert=" + walletIdCurrencyConvert +
                ", currencyConvertBalance=" + currencyConvertBalance +
                ", operationType=" + operationType +
                ", exchangeRate=" + exchangeRate +
                ", spentWalletBalance=" + spentWalletBalance +
                ", spentAmount=" + spentAmount +
                ", amount=" + amount +
                ", total=" + total +
                ", comissionId=" + comissionId +
                ", comission=" + comission +
                ", totalWithComission=" + totalWithComission +
                '}';
    }

    /*getters setters*/
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    public int getComissionForBuyId() {
        return comissionForBuyId;
    }

    public void setComissionForBuyId(int comissionForBuyId) {
        this.comissionForBuyId = comissionForBuyId;
    }

    public BigDecimal getComissionForBuyRate() {
        return normalize(comissionForBuyRate);
    }

    public void setComissionForBuyRate(BigDecimal comissionForBuyRate) {
        this.comissionForBuyRate = comissionForBuyRate;
    }

    public int getComissionForSellId() {
        return comissionForSellId;
    }

    public void setComissionForSellId(int comissionForSellId) {
        this.comissionForSellId = comissionForSellId;
    }

    public BigDecimal getComissionForSellRate() {
        return normalize(comissionForSellRate);
    }

    public void setComissionForSellRate(BigDecimal comissionForSellRate) {
        this.comissionForSellRate = comissionForSellRate;
    }

    public int getWalletIdCurrencyBase() {
        return walletIdCurrencyBase;
    }

    public void setWalletIdCurrencyBase(int walletIdCurrencyBase) {
        this.walletIdCurrencyBase = walletIdCurrencyBase;
    }

    public BigDecimal getCurrencyBaseBalance() {
        return normalize(currencyBaseBalance);
    }

    public void setCurrencyBaseBalance(BigDecimal currencyBaseBalance) {
        this.currencyBaseBalance = currencyBaseBalance;
    }

    public int getWalletIdCurrencyConvert() {
        return walletIdCurrencyConvert;
    }

    public void setWalletIdCurrencyConvert(int walletIdCurrencyConvert) {
        this.walletIdCurrencyConvert = walletIdCurrencyConvert;
    }

    public BigDecimal getCurrencyConvertBalance() {
        return normalize(currencyConvertBalance);
    }

    public void setCurrencyConvertBalance(BigDecimal currencyConvertBalance) {
        this.currencyConvertBalance = currencyConvertBalance;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public BigDecimal getExchangeRate() {
        return normalize(exchangeRate);
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getSpentWalletBalance() {
        return normalize(spentWalletBalance);
    }

    public void setSpentWalletBalance(BigDecimal balance) {
        this.spentWalletBalance = balance;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = normalize(spentAmount);
    }

    public BigDecimal getAmount() {
        return normalize(amount);
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTotal() {
        return normalize(total);
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public int getComissionId() {
        return comissionId;
    }

    public void setComissionId(int comissionId) {
        this.comissionId = comissionId;
    }

    public BigDecimal getComission() {
        return (comission);
    }

    public void setComission(BigDecimal comission) {
        this.comission = comission;
    }

    public BigDecimal getTotalWithComission() {
        return normalize(totalWithComission);
    }

    public void setTotalWithComission(BigDecimal totalWithComission) {
        this.totalWithComission = totalWithComission;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public BigDecimal getStop() {
        return stop;
    }

    public void setStop(BigDecimal stop) {
        this.stop = stop;
    }

    public OrderBaseType getOrderBaseType() {
        return orderBaseType;
    }

    public void setOrderBaseType(OrderBaseType orderBaseType) {
        this.orderBaseType = orderBaseType;
    }
}
