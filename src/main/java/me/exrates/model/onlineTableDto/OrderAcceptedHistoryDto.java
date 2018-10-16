package me.exrates.model.onlineTableDto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.OperationType;

import java.sql.Timestamp;

/**
 * Created by Valk on 03.06.2016.
 */
@Getter
@Setter
public class OrderAcceptedHistoryDto extends OnlineTableDto {
    private Integer orderId;
    private String dateAcceptionTime;
    private Timestamp acceptionTime;
    private String rate;
    private String amountBase;
    private OperationType operationType;

    public OrderAcceptedHistoryDto() {
        this.needRefresh = true;
    }

    public OrderAcceptedHistoryDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public OrderAcceptedHistoryDto(OrderAcceptedHistoryDto orderAcceptedHistoryDto) {
        this.needRefresh = orderAcceptedHistoryDto.needRefresh;
        this.page = orderAcceptedHistoryDto.page;
        this.orderId = orderAcceptedHistoryDto.orderId;
        this.dateAcceptionTime = orderAcceptedHistoryDto.dateAcceptionTime;
        this.acceptionTime = orderAcceptedHistoryDto.acceptionTime;
        this.rate = orderAcceptedHistoryDto.rate;
        this.orderId = orderAcceptedHistoryDto.orderId;
        this.amountBase = orderAcceptedHistoryDto.amountBase;
        this.operationType = orderAcceptedHistoryDto.operationType;
    }

    @Override
    public int hashCode() {
        return orderId != null ? orderId.hashCode() : 0;
    }

}
