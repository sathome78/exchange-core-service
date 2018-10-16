package me.exrates.model.onlineTableDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyWalletsDetailedDto extends OnlineTableDto {
    private Integer id;
    private Integer userId;
    private Integer currencyId;
    private String currencyName;
    private String currencyDescription;
    private String activeBalance;
    private String onConfirmation;
    private String onConfirmationStage;
    private String onConfirmationCount;
    private String reservedBalance;
    private String reservedByOrders;
    private String reservedByMerchant;

    public MyWalletsDetailedDto() {
        this.needRefresh = true;
    }

    public MyWalletsDetailedDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyWalletsDetailedDto that = (MyWalletsDetailedDto) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (currencyId != null ? !currencyId.equals(that.currencyId) : that.currencyId != null) return false;
        if (currencyName != null ? !currencyName.equals(that.currencyName) : that.currencyName != null) return false;
        if (currencyDescription != null ? !currencyDescription.equals(that.currencyDescription) : that.currencyDescription != null)
            return false;
        if (activeBalance != null ? !activeBalance.equals(that.activeBalance) : that.activeBalance != null)
            return false;
        if (onConfirmation != null ? !onConfirmation.equals(that.onConfirmation) : that.onConfirmation != null)
            return false;
        if (onConfirmationStage != null ? !onConfirmationStage.equals(that.onConfirmationStage) : that.onConfirmationStage != null)
            return false;
        if (onConfirmationCount != null ? !onConfirmationCount.equals(that.onConfirmationCount) : that.onConfirmationCount != null)
            return false;
        if (reservedBalance != null ? !reservedBalance.equals(that.reservedBalance) : that.reservedBalance != null)
            return false;
        if (reservedByOrders != null ? !reservedByOrders.equals(that.reservedByOrders) : that.reservedByOrders != null)
            return false;
        return reservedByMerchant != null ? reservedByMerchant.equals(that.reservedByMerchant) : that.reservedByMerchant == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (currencyId != null ? currencyId.hashCode() : 0);
        result = 31 * result + (currencyName != null ? currencyName.hashCode() : 0);
        result = 31 * result + (currencyDescription != null ? currencyDescription.hashCode() : 0);
        result = 31 * result + (activeBalance != null ? activeBalance.hashCode() : 0);
        result = 31 * result + (onConfirmation != null ? onConfirmation.hashCode() : 0);
        result = 31 * result + (onConfirmationStage != null ? onConfirmationStage.hashCode() : 0);
        result = 31 * result + (onConfirmationCount != null ? onConfirmationCount.hashCode() : 0);
        result = 31 * result + (reservedBalance != null ? reservedBalance.hashCode() : 0);
        result = 31 * result + (reservedByOrders != null ? reservedByOrders.hashCode() : 0);
        result = 31 * result + (reservedByMerchant != null ? reservedByMerchant.hashCode() : 0);
        return result;
    }

}
