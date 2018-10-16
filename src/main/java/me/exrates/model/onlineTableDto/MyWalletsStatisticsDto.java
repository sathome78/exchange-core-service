package me.exrates.model.onlineTableDto;

/**
 * Created by Valk
 */
public class MyWalletsStatisticsDto extends OnlineTableDto {
    private String currencyName;
    private String description;
    private String activeBalance;
    private String totalBalance;

    public MyWalletsStatisticsDto() {
        this.needRefresh = true;
    }

    public MyWalletsStatisticsDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    /*hash*/

    @Override
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

    /*getters setters*/

    public boolean isNeedRefresh() {
        return needRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getActiveBalance() {
        return activeBalance;
    }

    public void setActiveBalance(String activeBalance) {
        this.activeBalance = activeBalance;
    }

    public String getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(String totalBalance) {
        this.totalBalance = totalBalance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
