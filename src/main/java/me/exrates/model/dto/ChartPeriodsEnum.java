package me.exrates.model.dto;

import me.exrates.model.enums.IntervalType;
import me.exrates.model.main.BackDealInterval;

import java.util.Arrays;

public enum ChartPeriodsEnum {

    HOURS_12(new BackDealInterval(12, IntervalType.HOUR)),
    HOURS_24(new BackDealInterval(24, IntervalType.HOUR)),
    DAY_7(new BackDealInterval(7, IntervalType.DAY)),
    MONTH_1(new BackDealInterval(1, IntervalType.MONTH)),
    MONTH_6(new BackDealInterval(6, IntervalType.MONTH));

    private BackDealInterval backDealInterval;


    public BackDealInterval getBackDealInterval() {
        return backDealInterval;
    }

    ChartPeriodsEnum(BackDealInterval backDealInterval) {
        this.backDealInterval = backDealInterval;
    }

    public static ChartPeriodsEnum convert(String paramName) {
        return Arrays.stream(ChartPeriodsEnum.values())
                .filter(p -> p.backDealInterval.getInterval().equals(paramName))
                .findAny().orElseThrow(() -> new RuntimeException("Unsupported type of authority"));
    }
}
