package me.exrates.model.main;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by Valk on 19.04.16.
 */
@Component
@Getter
@Setter
public class ExOrder {
    private int id;
    private int userId;
    private int currencyPairId;
    private OperationType operationType;
    private BigDecimal exRate;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;
    private int comissionId;
    private BigDecimal commissionFixedAmount;
    private int userAcceptorId;
    private LocalDateTime dateCreation;
    private LocalDateTime dateAcception;
    private OrderStatus status;
    private CurrencyPair currencyPair;
    private Integer sourceId;
    private BigDecimal stop;
    private OrderBaseType orderBaseType = OrderBaseType.LIMIT;

    /*constructors*/
    public ExOrder() {
    }

    public ExOrder(OrderCreateDto orderCreateDto) {
        this.id = orderCreateDto.getOrderId();
        this.userId = orderCreateDto.getUserId();
        this.currencyPairId = orderCreateDto.getCurrencyPair().getId();
        this.operationType = orderCreateDto.getOperationType();
        this.exRate = orderCreateDto.getExchangeRate();
        this.amountBase = orderCreateDto.getAmount();
        this.amountConvert = orderCreateDto.getTotal();
        this.comissionId = orderCreateDto.getComissionId();
        this.commissionFixedAmount = orderCreateDto.getComission();
        this.status = orderCreateDto.getStatus();
        this.currencyPair = orderCreateDto.getCurrencyPair();
        this.sourceId = orderCreateDto.getSourceId();
        this.stop = orderCreateDto.getStop();
        this.orderBaseType = orderCreateDto.getOrderBaseType();
    }

    /*hash equals*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExOrder exOrder = (ExOrder) o;

        if (id != exOrder.id) return false;
        if (userId != exOrder.userId) return false;
        if (currencyPairId != exOrder.currencyPairId) return false;
        if (comissionId != exOrder.comissionId) return false;
        if (userAcceptorId != exOrder.userAcceptorId) return false;
        if (operationType != exOrder.operationType) return false;
        if (exRate != null ? !exRate.equals(exOrder.exRate) : exOrder.exRate != null) return false;
        if (amountBase != null ? !amountBase.equals(exOrder.amountBase) : exOrder.amountBase != null) return false;
        if (amountConvert != null ? !amountConvert.equals(exOrder.amountConvert) : exOrder.amountConvert != null)
            return false;
        if (commissionFixedAmount != null ? !commissionFixedAmount.equals(exOrder.commissionFixedAmount) : exOrder.commissionFixedAmount != null)
            return false;
        if (dateCreation != null ? !dateCreation.equals(exOrder.dateCreation) : exOrder.dateCreation != null)
            return false;
        if (dateAcception != null ? !dateAcception.equals(exOrder.dateAcception) : exOrder.dateAcception != null)
            return false;
        return status == exOrder.status;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + userId;
        result = 31 * result + currencyPairId;
        result = 31 * result + (operationType != null ? operationType.hashCode() : 0);
        result = 31 * result + (exRate != null ? exRate.hashCode() : 0);
        result = 31 * result + (amountBase != null ? amountBase.hashCode() : 0);
        result = 31 * result + (amountConvert != null ? amountConvert.hashCode() : 0);
        result = 31 * result + comissionId;
        result = 31 * result + (commissionFixedAmount != null ? commissionFixedAmount.hashCode() : 0);
        result = 31 * result + userAcceptorId;
        result = 31 * result + (dateCreation != null ? dateCreation.hashCode() : 0);
        result = 31 * result + (dateAcception != null ? dateAcception.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExOrder{" +
                "id=" + id +
                ", userId=" + userId +
                ", currencyPairId=" + currencyPairId +
                ", operationType=" + operationType +
                ", exRate=" + exRate +
                ", amountBase=" + amountBase +
                ", amountConvert=" + amountConvert +
                ", comissionId=" + comissionId +
                ", commissionFixedAmount=" + commissionFixedAmount +
                ", userAcceptorId=" + userAcceptorId +
                ", dateCreation=" + dateCreation +
                ", dateAcception=" + dateAcception +
                ", status=" + status +
                '}';
    }
}
