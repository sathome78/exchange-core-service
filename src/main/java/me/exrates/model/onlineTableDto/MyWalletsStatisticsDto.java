package me.exrates.model.onlineTableDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyWalletsStatisticsDto extends OnlineTableDto {
    private String currencyName;
    private String description;
    private String activeBalance;
    private String totalBalance;

    public MyWalletsStatisticsDto() {
        this.needRefresh = true;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyWalletsStatisticsDto that = (MyWalletsStatisticsDto) o;

        if (currencyName != null ? !currencyName.equals(that.currencyName) : that.currencyName != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (activeBalance != null ? !activeBalance.equals(that.activeBalance) : that.activeBalance != null)
            return false;
        return totalBalance != null ? totalBalance.equals(that.totalBalance) : that.totalBalance == null;
    }

    @Override
    public int hashCode() {
        int result = currencyName != null ? currencyName.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (activeBalance != null ? activeBalance.hashCode() : 0);
        result = 31 * result + (totalBalance != null ? totalBalance.hashCode() : 0);
        return result;
    }

}
