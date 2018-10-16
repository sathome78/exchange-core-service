package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class CurrencyPairLimitDto {
  private Integer currencyPairId;
  private String currencyPairName;
  private BigDecimal minRate;
  private BigDecimal maxRate;
  private BigDecimal minAmount;
  private BigDecimal maxAmount;
}
