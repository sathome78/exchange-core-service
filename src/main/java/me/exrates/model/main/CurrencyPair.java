package me.exrates.model.main;

import me.exrates.model.enums.CurrencyPairType;
import org.springframework.stereotype.Component;

@Component
public class CurrencyPair {
    private int id;
    private String name;
    private Currency currency1;
    private Currency currency2;
    private String market;
    private String marketName;
    private CurrencyPairType pairType;

    /*constructors*/
    public CurrencyPair() {
    }

    public CurrencyPair(Currency currency1, Currency currency2) {
        this.currency1 = currency1;
        this.currency2 = currency2;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrencyPair that = (CurrencyPair) o;

        if (currency1 != null ? !currency1.equals(that.currency1) : that.currency1 != null) return false;
        if (currency2 != null ? !currency2.equals(that.currency2) : that.currency2 != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    public int hashCode() {
        int result = currency1 != null ? currency1.hashCode() : 0;
        result = 31 * result + (currency2 != null ? currency2.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CurrencyPair{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", currency1=" + currency1 +
                ", currency2=" + currency2 +
                ", market='" + market + '\'' +
                ", marketName='" + marketName + '\'' +
                ", pairType=" + pairType +
                '}';
    }

    /*getters setters*/
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Currency getCurrency1() {
        return currency1;
    }

    public void setCurrency1(Currency currency1) {
        this.currency1 = currency1;
    }

    public Currency getCurrency2() {
        return currency2;
    }

    public void setCurrency2(Currency currency2) {
        this.currency2 = currency2;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public CurrencyPairType getPairType() {
        return pairType;
    }

    public void setPairType(CurrencyPairType pairType) {
        this.pairType = pairType;
    }
}
