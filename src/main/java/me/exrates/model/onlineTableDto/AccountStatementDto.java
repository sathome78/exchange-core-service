package me.exrates.model.onlineTableDto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.model.enums.TransactionStatus;
import me.exrates.util.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class AccountStatementDto extends OnlineTableDto {
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime datetime;
    private Integer transactionId;
    private String activeBalanceBefore;
    private String reservedBalanceBefore;
    private String operationType;
    private String amount;
    private String commissionAmount;
    private String activeBalanceAfter;
    private String reservedBalanceAfter;
    private String sourceType;
    private String sourceTypeId;
    private Integer sourceId;
    private TransactionStatus transactionStatus;
    private String merchantName;
    private Boolean checked;
    private Integer walletId;
    private Integer userId;

    public AccountStatementDto() {
        this.needRefresh = true;
    }

    public AccountStatementDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    /*hash*/

    @Override
    public int hashCode() {
        int result = transactionId != null ? transactionId.hashCode() : 0;
        result = 31 * result + (activeBalanceBefore != null ? activeBalanceBefore.hashCode() : 0);
        result = 31 * result + (reservedBalanceBefore != null ? reservedBalanceBefore.hashCode() : 0);
        result = 31 * result + (operationType != null ? operationType.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (commissionAmount != null ? commissionAmount.hashCode() : 0);
        result = 31 * result + (sourceId != null ? sourceId.hashCode() : 0);
        result = 31 * result + (transactionStatus != null ? transactionStatus.hashCode() : 0);
        return result;
    }

    /*getters setters*/

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getActiveBalanceBefore() {
        return activeBalanceBefore;
    }

    public void setActiveBalanceBefore(String activeBalanceBefore) {
        this.activeBalanceBefore = activeBalanceBefore;
    }

    public String getReservedBalanceBefore() {
        return reservedBalanceBefore;
    }

    public void setReservedBalanceBefore(String reservedBalanceBefore) {
        this.reservedBalanceBefore = reservedBalanceBefore;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(String commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getActiveBalanceAfter() {
        return activeBalanceAfter;
    }

    public void setActiveBalanceAfter(String activeBalanceAfter) {
        this.activeBalanceAfter = activeBalanceAfter;
    }

    public String getReservedBalanceAfter() {
        return reservedBalanceAfter;
    }

    public void setReservedBalanceAfter(String reservedBalanceAfter) {
        this.reservedBalanceAfter = reservedBalanceAfter;
    }

    public String getSourceTypeId() {
        return sourceTypeId;
    }

    public void setSourceTypeId(String sourceTypeId) {
        this.sourceTypeId = sourceTypeId;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Integer getWalletId() {
        return walletId;
    }

    public void setWalletId(Integer walletId) {
        this.walletId = walletId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "AccountStatementDto{" +
                "datetime=" + datetime +
                ", transactionId=" + transactionId +
                ", activeBalanceBefore='" + activeBalanceBefore + '\'' +
                ", reservedBalanceBefore='" + reservedBalanceBefore + '\'' +
                ", operationType='" + operationType + '\'' +
                ", amount='" + amount + '\'' +
                ", commissionAmount='" + commissionAmount + '\'' +
                ", activeBalanceAfter='" + activeBalanceAfter + '\'' +
                ", reservedBalanceAfter='" + reservedBalanceAfter + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", sourceTypeId='" + sourceTypeId + '\'' +
                ", sourceId=" + sourceId +
                ", transactionStatus=" + transactionStatus +
                ", merchantName='" + merchantName + '\'' +
                ", checked=" + checked +
                '}';
    }
}
