package me.exrates.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.CurrencyPairType;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class StatisticForMarket {

    public int currencyPairId;
    public String currencyPairName;
    private String market;
    private BigDecimal lastOrderRate;
    private BigDecimal predLastOrderRate;
    private BigDecimal volume;
    private String percentChange;
    private int page = 0;
    private boolean needToRefresh = true;
    private CurrencyPairType type;
}