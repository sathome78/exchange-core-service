package me.exrates.model;

import me.exrates.model.main.BackDealInterval;

import java.util.Arrays;

public enum OrderHistoryPeriod {
    MINUTE(new BackDealInterval("1 MINUTE")),
    HOUR(new BackDealInterval("1 HOUR")),
    DAY(new BackDealInterval("1 DAY"));

    private BackDealInterval interval;

    OrderHistoryPeriod(BackDealInterval interval) {
        this.interval = interval;
    }

    public BackDealInterval getInterval() {
        return interval;
    }

    public String toUrlValue() {
        return this.name().toLowerCase();
    }

    public static OrderHistoryPeriod fromLowerCaseString(String lowerCaseString) {
        String upperCaseString = lowerCaseString.toUpperCase();
        return Arrays.stream(OrderHistoryPeriod.values()).filter(val -> val.name().equals(upperCaseString))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(upperCaseString));
    }
}
