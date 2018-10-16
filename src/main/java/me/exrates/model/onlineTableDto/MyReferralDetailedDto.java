package me.exrates.model.onlineTableDto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.util.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class MyReferralDetailedDto extends OnlineTableDto {
    private Integer transactionId;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateTransaction;
    private String amount;
    private String currencyName;
    private Integer referralId;
    private Integer referralLevel;
    private String referralPercent;
    private String initiatorEmail;
    private String status;

    public MyReferralDetailedDto() {
        this.needRefresh = true;
    }

    public MyReferralDetailedDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    /*hash*/

    @Override
    public int hashCode() {
        int result = transactionId != null ? transactionId.hashCode() : 0;
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }

    /*getters setters*/

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(LocalDateTime dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getInitiatorEmail() {
        return initiatorEmail;
    }

    public void setInitiatorEmail(String initiatorEmail) {
        this.initiatorEmail = initiatorEmail;
    }

    public Integer getReferralId() {
        return referralId;
    }

    public void setReferralId(Integer referralId) {
        this.referralId = referralId;
    }

    public Integer getReferralLevel() {
        return referralLevel;
    }

    public void setReferralLevel(Integer referralLevel) {
        this.referralLevel = referralLevel;
    }

    public String getReferralPercent() {
        return referralPercent;
    }

    public void setReferralPercent(String referralPercent) {
        this.referralPercent = referralPercent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
