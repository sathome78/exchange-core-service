package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.main.CurrencyPair;

@Getter
@Setter
@ToString
public class CurrentParams {
    private CurrencyPair currencyPair;
    private String period;
    private String chartType;
    private Boolean showAllPairs;
    private Boolean orderRoleFilterEnabled;


}
