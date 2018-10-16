package me.exrates.dao;

import me.exrates.model.dto.CurrencyInputOutputSummaryDto;
import me.exrates.model.dto.InputOutputCommissionSummaryDto;
import me.exrates.model.dto.PaginationWrapper;
import me.exrates.model.onlineTableDto.MyInputOutputHistoryDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public interface InputOutputDao {

    List<MyInputOutputHistoryDto> findMyInputOutputHistoryByOperationType(
            String email,
            Integer offset,
            Integer limit,
            List<Integer> operationTypeIdList,
            Locale locale);

    PaginationWrapper<List<MyInputOutputHistoryDto>> findUnconfirmedInvoices(Integer userId, Integer currencyId, Integer limit, Integer offset);

    List<CurrencyInputOutputSummaryDto> getInputOutputSummary(LocalDateTime startTime, LocalDateTime endTime, List<Integer> userRoleIdList);

    List<InputOutputCommissionSummaryDto> getInputOutputSummaryWithCommissions(LocalDateTime startTime, LocalDateTime endTime, List<Integer> userRoleIdList);
}
