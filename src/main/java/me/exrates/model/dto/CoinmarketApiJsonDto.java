package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.util.BigDecimalNonePointSerializer;

import java.math.BigDecimal;


@Getter
@Setter
@EqualsAndHashCode
@ToString

public class CoinmarketApiJsonDto {
    private Integer id;
    @JsonSerialize(using = BigDecimalNonePointSerializer.class)
    private BigDecimal last;
    @JsonSerialize(using = BigDecimalNonePointSerializer.class)
    private BigDecimal lowestAsk;
    @JsonSerialize(using = BigDecimalNonePointSerializer.class)
    private BigDecimal highestBid;
    @JsonSerialize(using = BigDecimalNonePointSerializer.class)
    private BigDecimal percentChange;
    @JsonSerialize(using = BigDecimalNonePointSerializer.class)
    private BigDecimal baseVolume;
    @JsonSerialize(using = BigDecimalNonePointSerializer.class)
    private BigDecimal quoteVolume;
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer isFrozen;
    @JsonSerialize(using = BigDecimalNonePointSerializer.class)
    private BigDecimal high24hr;
    @JsonSerialize(using = BigDecimalNonePointSerializer.class)
    private BigDecimal low24hr;

    public CoinmarketApiJsonDto(CoinmarketApiDto coinmarketApiDto) {
        this.id = coinmarketApiDto.getCurrencyPairId();
        this.last = coinmarketApiDto.getLast();
        this.lowestAsk = coinmarketApiDto.getLowestAsk();
        this.highestBid = coinmarketApiDto.getHighestBid();
        this.percentChange = coinmarketApiDto.getPercentChange();
        this.baseVolume = coinmarketApiDto.getBaseVolume();
        this.quoteVolume = coinmarketApiDto.getQuoteVolume();
        this.isFrozen = coinmarketApiDto.getIsFrozen();
        this.high24hr = coinmarketApiDto.getHigh24hr();
        this.low24hr = coinmarketApiDto.getLow24hr();
    }
}
