package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.chart.ChartTimeFrame;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
public class ChartTimeFrameDto {
    private String text;
    private String resolution;
    private int resolutionSeconds;
    private String description;
    private Integer timeValue;
    private String timeUnit;

    public ChartTimeFrameDto(ChartTimeFrame chartTimeFrame) {
        this.text = chartTimeFrame.getShortName();
        this.resolution = chartTimeFrame.getResolution().toString();
        this.resolutionSeconds = chartTimeFrame.getResolution().getSeconds();
        this.description = null;
        this.timeValue = chartTimeFrame.getTimeValue();
        this.timeUnit = chartTimeFrame.getTimeUnit().getShortName();
    }
}
