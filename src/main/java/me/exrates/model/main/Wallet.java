package me.exrates.model.main;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.User;
import me.exrates.util.BigDecimalProcessing;

import java.math.BigDecimal;

@Getter
@ToString
@Setter
public class Wallet {

    private int id;
    private int currencyId;
    private User user;
    private BigDecimal activeBalance;
    private BigDecimal reservedBalance;
    private String name;

    public Wallet() {

    }

    public Wallet(int currencyId, User user, BigDecimal activeBalance) {
        this.currencyId = currencyId;
        this.user = user;
        this.activeBalance = activeBalance;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Wallet wallet = (Wallet) o;

        if (id != wallet.id) return false;
        if (currencyId != wallet.currencyId) return false;
        if (!user.equals(wallet.user)) return false;
        if (activeBalance != null ? !activeBalance.equals(wallet.activeBalance) : wallet.activeBalance != null)
            return false;
        if (reservedBalance != null ? !reservedBalance.equals(wallet.reservedBalance) : wallet.reservedBalance != null)
            return false;
        return name != null ? name.equals(wallet.name) : wallet.name == null;

    }

    public int hashCode() {
        int result;
        result = id;
        result = 31 * result + currencyId;
        result = 31 * result + (activeBalance != null ? activeBalance.hashCode() : 0);
        result = 31 * result + (reservedBalance != null ? reservedBalance.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

}