package me.exrates.cache;

import me.exrates.model.dto.CandleChartItemDto;

import java.util.List;

/**
 * Created by Maks on 29.01.2018.
 */
public interface ChartsCacheInterface {

    List<CandleChartItemDto> getData();

    List<CandleChartItemDto> getLastData();

    void setNeedToUpdate();
}
