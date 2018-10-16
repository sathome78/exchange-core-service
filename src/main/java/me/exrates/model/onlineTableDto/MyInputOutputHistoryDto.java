package me.exrates.model.onlineTableDto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.enums.*;
import me.exrates.util.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static me.exrates.model.enums.TransactionSourceType.*;

/**
 * Created by Ajet on 23.07.2016.
 */
@Log4j2
@Getter
@Setter
@ToString
public class MyInputOutputHistoryDto extends OnlineTableDto {
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime datetime;
    private String currencyName;
    private String amount;
    private String commissionAmount;
    private String merchantName;
    private String operationType;
    private Integer transactionId;
    private Integer provided;
    private String transactionProvided;
    private Integer id;
    private String destination;
    private Integer userId;
    private String bankAccount;
    private InvoiceStatus status;
    private LocalDateTime statusUpdateDate;
    private String summaryStatus;
    private String userFullName;
    private String remark;
    private TransactionSourceType sourceType;
    private Integer sourceId;
    private Integer confirmation;
    private Integer adminHolderId;
    private Integer authorisedUserId;
    private List<Map<String, Object>> buttons;
    private String transactionHash;


    public MyInputOutputHistoryDto() {
        this.needRefresh = true;
    }

    public MyInputOutputHistoryDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public void setStatus(Integer statusId) {
        log.debug("status is {}, sourceType {}, id {}", statusId, sourceType, id);
        if (sourceType == REFILL) {
            this.status = RefillStatusEnum.convert(statusId);
        } else if (sourceType == WITHDRAW) {
            this.status = WithdrawStatusEnum.convert(statusId);
        } else if (sourceType == USER_TRANSFER) {
            this.status = TransferStatusEnum.convert(statusId);
        }
    }

    public void setSourceId(String sourceType) {
        this.sourceId = TransactionSourceType.convert(sourceType).getCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyInputOutputHistoryDto that = (MyInputOutputHistoryDto) o;

        if (datetime != null ? !datetime.equals(that.datetime) : that.datetime != null) return false;
        if (currencyName != null ? !currencyName.equals(that.currencyName) : that.currencyName != null) return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (commissionAmount != null ? !commissionAmount.equals(that.commissionAmount) : that.commissionAmount != null)
            return false;
        if (merchantName != null ? !merchantName.equals(that.merchantName) : that.merchantName != null) return false;
        if (operationType != null ? !operationType.equals(that.operationType) : that.operationType != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;
        if (status != null ? !status.equals(that.status) : that.status != null)
            return false;
        if (confirmation != null ? !confirmation.equals(that.confirmation) : that.confirmation != null)
            return false;
        if (adminHolderId != null ? !adminHolderId.equals(that.adminHolderId) : that.adminHolderId != null)
            return false;
        return provided != null ? provided.equals(that.provided) : that.provided == null;

    }

    @Override
    public int hashCode() {
        int result = datetime != null ? datetime.hashCode() : 0;
        result = 31 * result + (currencyName != null ? currencyName.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (commissionAmount != null ? commissionAmount.hashCode() : 0);
        result = 31 * result + (merchantName != null ? merchantName.hashCode() : 0);
        result = 31 * result + (operationType != null ? operationType.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (provided != null ? provided.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (confirmation != null ? confirmation.hashCode() : 0);
        result = 31 * result + (adminHolderId != null ? adminHolderId.hashCode() : 0);
        return result;
    }

}
