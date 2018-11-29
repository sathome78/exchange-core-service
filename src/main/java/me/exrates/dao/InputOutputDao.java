package me.exrates.dao;

import me.exrates.model.dto.CurrencyInputOutputSummaryDto;
import me.exrates.model.onlineTableDto.MyInputOutputHistoryDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public interface InputOutputDao {
    List<MyInputOutputHistoryDto> findMyInputOutputHistoryByOperationType(
            String email,
            Integer offset,
            Integer limit,
            String dateFrom, String dateTo, List<Integer> operationTypeIdList,
            Locale locale, String currency);

    List<CurrencyInputOutputSummaryDto> getInputOutputSummary(LocalDateTime startTime, LocalDateTime endTime, List<Integer> userRoleIdList);

    List<MyInputOutputHistoryDto> findMyInputOutputHistoryByOperationType(String email, Integer offset, Integer limit, List<Integer> operationTypeList, Locale locale);
}
