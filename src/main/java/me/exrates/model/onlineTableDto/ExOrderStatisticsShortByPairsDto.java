package me.exrates.model.onlineTableDto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.CurrencyPairType;

/**
 * Created by Valk
 */
@Getter
@Setter
@ToString
public class ExOrderStatisticsShortByPairsDto extends OnlineTableDto {
  private String currencyPairName;
  private String lastOrderRate;
  private String predLastOrderRate;
  private String percentChange;
  @JsonIgnore
  private Integer currencyPairId;
  @JsonIgnore
  private CurrencyPairType type;

  public ExOrderStatisticsShortByPairsDto() {
    this.needRefresh = true;
  }

  public ExOrderStatisticsShortByPairsDto(boolean needRefresh) {
    this.needRefresh = needRefresh;
  }

  public ExOrderStatisticsShortByPairsDto(ExOrderStatisticsShortByPairsDto exOrderStatisticsShortByPairsDto) {
    this.needRefresh = exOrderStatisticsShortByPairsDto.needRefresh;
    this.page = exOrderStatisticsShortByPairsDto.page;
    this.currencyPairName = exOrderStatisticsShortByPairsDto.currencyPairName;
    this.lastOrderRate = exOrderStatisticsShortByPairsDto.lastOrderRate;
    this.predLastOrderRate = exOrderStatisticsShortByPairsDto.predLastOrderRate;
    this.percentChange = exOrderStatisticsShortByPairsDto.percentChange;
    this.type = exOrderStatisticsShortByPairsDto.type;
    this.currencyPairId = exOrderStatisticsShortByPairsDto.currencyPairId;
  }

  public ExOrderStatisticsShortByPairsDto(Integer pairId, String lastRate, String predLastRate) {
    this.lastOrderRate = lastRate;
    this.predLastOrderRate = predLastRate;
    this.currencyPairId = pairId;
  }

  @Override
  public int hashCode() {
    int result = currencyPairName != null ? currencyPairName.hashCode() : 0;
    result = 31 * result + (lastOrderRate != null ? lastOrderRate.hashCode() : 0);
    result = 31 * result + (predLastOrderRate != null ? predLastOrderRate.hashCode() : 0);
    return result;
  }

}
