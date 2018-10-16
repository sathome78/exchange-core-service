package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.onlineTableDto.OnlineTableDto;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@ToString
public class WalletTotalUsdDto extends OnlineTableDto {

    public WalletTotalUsdDto(String currency) {
        this.currency = currency;
    }

    private Map<String, BigDecimal> rates;
    private String currency;
    private BigDecimal totalBalance;
    private BigDecimal sumUSD;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WalletTotalUsdDto that = (WalletTotalUsdDto) o;

        return currency != null ? currency.equals(that.currency) : that.currency == null;
    }

    @Override
    public int hashCode() {
        return currency != null ? currency.hashCode() : 0;
    }
}
