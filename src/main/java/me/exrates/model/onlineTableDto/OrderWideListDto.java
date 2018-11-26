package me.exrates.model.onlineTableDto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.util.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderWideListDto extends OnlineTableDto {
    private int id;
    private int userId;
    private String operationType;
    private OperationType operationTypeEnum;
    private String stopRate; /*for stop orders*/
    private String exExchangeRate;
    private String amountBase;
    private String amountConvert;
    private int comissionId;
    private String commissionFixedAmount;
    private String amountWithCommission;
    private int userAcceptorId;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateCreation;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateAcception;
    private OrderStatus status;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateStatusModification;
    private String commissionAmountForAcceptor;
    private String amountWithCommissionForAcceptor;
    private int currencyPairId;
    private String currencyPairName;
    private String statusString;
    private OrderBaseType orderBaseType;
    private Double commissionValue;

    public OrderWideListDto() {
        this.needRefresh = true;
    }

    public OrderWideListDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public int hashCode() {
        int result = id;
        result = 31 * result + (dateAcception != null ? dateAcception.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
