package me.exrates.cache;

import me.exrates.model.onlineTableDto.ExOrderStatisticsShortByPairsDto;

import java.util.List;

public interface ExchangeRatesHolder {

    List<ExOrderStatisticsShortByPairsDto> getAllRates();

    List<ExOrderStatisticsShortByPairsDto> getCurrenciesRates(List<Integer> id);
}
