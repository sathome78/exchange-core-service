package me.exrates.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.main.CurrencyPair;
import me.exrates.util.BigDecimalProcessing;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CoinmarketApiDto {

    private Integer currencyPairId;
    private String currency_pair_name;
    private BigDecimal first;
    private BigDecimal last;
    private BigDecimal lowestAsk;
    private BigDecimal highestBid;
    private BigDecimal percentChange;
    private BigDecimal baseVolume;
    private BigDecimal quoteVolume;
    private Integer isFrozen;
    private BigDecimal high24hr;
    private BigDecimal low24hr;

    public CoinmarketApiDto(CurrencyPair currencyPair) {
        this.currency_pair_name = currencyPair.getName();
    }

    @Override
    public String toString() {
        return '"' + currency_pair_name.replace('/', '_') + "\":" +
                "{\"last\":" + BigDecimalProcessing.formatNonePointQuoted(last, true) +
                ", \"lowestAsk\":" + BigDecimalProcessing.formatNonePointQuoted(lowestAsk, true) +
                ", \"highestBid\":" + BigDecimalProcessing.formatNonePointQuoted(highestBid, true) +
                ", \"percentChange\":" + BigDecimalProcessing.formatNonePointQuoted(percentChange, true) +
                ", \"baseVolume\":" + BigDecimalProcessing.formatNonePointQuoted(baseVolume, true) +
                ", \"quoteVolume\":" + BigDecimalProcessing.formatNonePointQuoted(quoteVolume, true) +
                ", \"isFrozen\":" + '"' + isFrozen + '"' +
                ", \"high24hr\":" + BigDecimalProcessing.formatNonePointQuoted(high24hr, true) +
                ", \"low24hr\":" + BigDecimalProcessing.formatNonePointQuoted(low24hr, true) +
                '}';
    }
}
