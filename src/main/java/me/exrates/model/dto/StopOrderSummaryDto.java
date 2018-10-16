package me.exrates.model.dto;

import lombok.Data;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

@Data
public class StopOrderSummaryDto {

    private int orderId;
    private BigDecimal stopRate;
    private OperationType operationType;

    public StopOrderSummaryDto(int orderId, BigDecimal stopRate) {
        this.orderId = orderId;
        this.stopRate = stopRate;
    }

    public StopOrderSummaryDto(int orderId, BigDecimal stopRate, OperationType operationType) {
        this.orderId = orderId;
        this.stopRate = stopRate;
        this.operationType = operationType;
    }
}
