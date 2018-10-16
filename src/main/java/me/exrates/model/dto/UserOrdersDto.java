package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.ToString;
import me.exrates.util.BigDecimalToDoubleSerializer;
import me.exrates.util.LocalDateTimeToLongSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@ToString
public class UserOrdersDto {

    private Integer id;

    @JsonProperty("currency_pair")
    private String currencyPair;

    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal amount;

    @JsonProperty("order_type")
    private String orderType;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal price;

    @JsonProperty("date_created")
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime dateCreation;

    @JsonProperty("date_accepted")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime dateAcceptance;

    public UserOrdersDto(Integer id, String currencyPair, BigDecimal amount, String orderType, BigDecimal price, LocalDateTime dateCreation, LocalDateTime dateAcceptance) {
        this.id = id;
        this.currencyPair = currencyPair.toLowerCase().replace('/', '_');
        this.amount = amount;
        this.orderType = orderType;
        this.price = price;
        this.dateCreation = dateCreation;
        this.dateAcceptance = dateAcceptance;
    }
}
