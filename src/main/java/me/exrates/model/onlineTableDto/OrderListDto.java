package me.exrates.model.onlineTableDto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.OperationType;
import org.springframework.util.StringUtils;

/**
 * Created by Valk on 14.04.16.
 */
@Getter
@Setter
public class OrderListDto extends OnlineTableDto {
    private int id;
    private int userId;
    private OperationType orderType;
    private String exrate;
    private String amountBase;
    private String amountConvert;
    private String ordersIds;

    public OrderListDto(String ordersIds, String exrate, String amountBase, String amountConvert, OperationType orderType, boolean needRefresh) {
        this.ordersIds = ordersIds;
        this.exrate = exrate;
        this.amountBase = amountBase;
        this.amountConvert = amountConvert;
        this.orderType = orderType;
        this.needRefresh = needRefresh;
        this.needRefresh = true;
    }

    public OrderListDto() {
        this.needRefresh = true;
    }

    public OrderListDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public OrderListDto(OrderListDto orderListDto) {
        this.needRefresh = orderListDto.needRefresh;
        this.page = orderListDto.page;
        this.id = orderListDto.id;
        this.userId = orderListDto.userId;
        this.orderType = orderListDto.orderType;
        this.exrate = orderListDto.exrate;
        this.amountBase = orderListDto.amountBase;
        this.amountConvert = orderListDto.amountConvert;
        this.ordersIds = orderListDto.getOrdersIds();
    }

    @Override
    public int hashCode() {
        return StringUtils.isEmpty(ordersIds) ? id : ordersIds.hashCode();
    }

}
