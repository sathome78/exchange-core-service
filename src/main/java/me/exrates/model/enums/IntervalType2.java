package me.exrates.model.enums;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;

public enum IntervalType2 {
    HOUR(ChronoUnit.HOURS, 3L, true, "H"),
    DAY(ChronoUnit.DAYS, 5L, true, "D"),
    WEAK(ChronoUnit.WEEKS, 20L, true, "W"),
    YEAR(ChronoUnit.YEARS, 86400L, true, "Y"),
    MONTH(ChronoUnit.MONTHS, 3600L, true, "M");

    private TemporalUnit correspondingTimeUnit;

    private Long chartRefreshInterval;

    private boolean chartLazyUpdate;

    private String shortName;

    IntervalType2(TemporalUnit correspondingTimeUnit, Long chartRefreshInterval, boolean chartLazyUpdate, String shortName) {
        this.correspondingTimeUnit = correspondingTimeUnit;
        this.chartRefreshInterval = chartRefreshInterval;
        this.chartLazyUpdate = chartLazyUpdate;
        this.shortName = shortName;
    }

    public TemporalUnit getCorrespondingTimeUnit() {
        return correspondingTimeUnit;
    }

    public boolean isChartLazyUpdate() {
        return chartLazyUpdate;
    }

    public String getShortName() {
        return shortName;
    }

    public static IntervalType2 fromShortName(String shortName) {
        return Arrays.stream(IntervalType2.values()).filter(item -> item.getShortName().equals(shortName))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(shortName));

    }

}
