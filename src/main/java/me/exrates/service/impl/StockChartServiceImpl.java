package me.exrates.service.impl;

import me.exrates.model.chart.ChartResolution;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.ChartTimeFrameDto;
import me.exrates.service.StockChartService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service

public class StockChartServiceImpl implements StockChartService {

    private Map<ChartResolution, ChartTimeFrame> timeFrames = new ConcurrentHashMap<>();

    @Override
    public Map<String, ChartTimeFrameDto> getTimeFramesByResolutions() {
        return timeFrames.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(),
                entry -> new ChartTimeFrameDto(entry.getValue())));
    }
}
