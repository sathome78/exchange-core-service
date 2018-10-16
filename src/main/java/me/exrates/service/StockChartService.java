package me.exrates.service;

import me.exrates.model.dto.ChartTimeFrameDto;

import java.util.Map;

public interface StockChartService {
    Map<String, ChartTimeFrameDto> getTimeFramesByResolutions();
}
