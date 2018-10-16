package me.exrates.cache;

import me.exrates.model.dto.CandleChartItemDto;

import java.util.List;

public interface ChartsCacheInterface {

    List<CandleChartItemDto> getData();

    List<CandleChartItemDto> getLastData();

    void setNeedToUpdate();
}
