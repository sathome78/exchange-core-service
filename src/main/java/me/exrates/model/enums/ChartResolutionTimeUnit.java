package me.exrates.model.enums;

import java.util.Arrays;

public enum ChartResolutionTimeUnit {
    MINUTE("", 60, 5), DAY("D", 86400, 120),WEAK("W", 604800, 1200);

   private String shortName;

   private Integer secondsValue;

   private Integer refreshDelaySeconds;

    ChartResolutionTimeUnit(String shortName, Integer secondsValue, Integer refreshDelaySeconds) {
        this.shortName = shortName;
        this.secondsValue = secondsValue;
        this.refreshDelaySeconds = refreshDelaySeconds;
    }

    public Integer getSecondsValue() {
        return secondsValue;
    }

    public String getShortName() {
        return shortName;
    }

    public Integer getRefreshDelaySeconds() {
        return refreshDelaySeconds;
    }

    public static ChartResolutionTimeUnit fromShortName(String shortName) {
        return Arrays.stream(ChartResolutionTimeUnit.values()).filter(item -> item.getShortName().equals(shortName)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(shortName));
    }
}
