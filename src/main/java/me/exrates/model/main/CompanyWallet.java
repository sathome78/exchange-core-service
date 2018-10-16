package me.exrates.model.main;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
@Setter
public class CompanyWallet {

    private int id;
    private Currency currency;
    private BigDecimal balance;
    private BigDecimal commissionBalance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompanyWallet that = (CompanyWallet) o;

        if (id != that.id) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (balance != null ? !balance.equals(that.balance) : that.balance != null) return false;
        return commissionBalance != null ? commissionBalance.equals(that.commissionBalance) : that.commissionBalance == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (commissionBalance != null ? commissionBalance.hashCode() : 0);
        return result;
    }

}