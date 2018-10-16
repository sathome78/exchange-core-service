package me.exrates.model.enums;

/**
 * Created by Maks on 30.08.2017.
 */
public enum RefreshObjectsEnum {

    MY_TRADES(null),
    ALL_TRADES(null),
    CHART(null),
    STATISTICS(null),
    CURRENCIES_STATISTIC(null),
    MAIN_CURRENCIES_STATISTIC("MAIN_CURRENCIES_STATISTIC"),
    ICO_CURRENCIES_STATISTIC("ICO_CURRENCIES_STATISTIC"),
    MAIN_CURRENCY_STATISTIC("MAIN_CURRENCIES_STATISTIC"),
    ICO_CURRENCY_STATISTIC("ICO_CURRENCIES_STATISTIC");

    private String subscribeChannel;

    RefreshObjectsEnum(String subscribeChannel) {
        this.subscribeChannel = subscribeChannel;
    }

    public String getSubscribeChannel() {
        return subscribeChannel;
    }
}
