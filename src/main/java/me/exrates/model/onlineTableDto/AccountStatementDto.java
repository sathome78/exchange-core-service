package me.exrates.model.onlineTableDto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.TransactionStatus;
import me.exrates.util.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
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
}
