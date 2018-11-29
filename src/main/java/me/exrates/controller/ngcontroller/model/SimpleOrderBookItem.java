package me.exrates.controller.ngcontroller.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import me.exrates.controller.ngcontroller.util.BigDecimalToStringSerializer;
import me.exrates.model.enums.OrderType;

import java.math.BigDecimal;

@Data
@Builder
public class SimpleOrderBookItem {

    private Integer currencyPairId;

    private OrderType orderType;

    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal exrate;

    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal amount;

    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal total;
}
