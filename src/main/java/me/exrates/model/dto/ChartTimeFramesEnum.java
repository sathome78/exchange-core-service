package me.exrates.model.dto;

import me.exrates.model.chart.ChartResolution;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.enums.ChartResolutionTimeUnit;

import java.util.Arrays;

import static me.exrates.model.enums.IntervalType2.DAY;
import static me.exrates.model.enums.IntervalType2.MONTH;


public enum ChartTimeFramesEnum {
    DAY_5(new ChartTimeFrame(new ChartResolution(30, ChartResolutionTimeUnit.MINUTE), 5, DAY)),
    DAY_7(new ChartTimeFrame(new ChartResolution(60, ChartResolutionTimeUnit.MINUTE), 7, DAY)),
    DAY_10(new ChartTimeFrame(new ChartResolution(240, ChartResolutionTimeUnit.MINUTE), 10, DAY)),
    DAY_15(new ChartTimeFrame(new ChartResolution(720, ChartResolutionTimeUnit.MINUTE), 15, DAY)),
    MONTH_7(new ChartTimeFrame(new ChartResolution(1, ChartResolutionTimeUnit.DAY), 7, MONTH));


    private ChartTimeFrame timeFrame;

    ChartTimeFramesEnum(ChartTimeFrame timeFrame) {
        this.timeFrame = timeFrame;
    }

    public ChartTimeFrame getTimeFrame() {
        return timeFrame;
    }

    public ChartResolution getResolution() {
        return timeFrame.getResolution();
    }

    public static ChartTimeFramesEnum ofResolution(String resolutionString) {
        ChartResolution resolution = ChartResolution.ofString(resolutionString);
        return Arrays.stream(ChartTimeFramesEnum.values()).filter(item -> item.getResolution().equals(resolution))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(resolutionString));
    }
}
