package me.exrates.service;

import me.exrates.model.enums.InvoiceOperationPermission;
import me.exrates.model.enums.InvoiceStatus;
import me.exrates.model.main.CacheData;
import me.exrates.model.onlineTableDto.MyInputOutputHistoryDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface InputOutputService {
    List<MyInputOutputHistoryDto> getMyInputOutputHistory(CacheData cacheData, String email, Integer offset, Integer limit, Locale locale);

    @Transactional(readOnly = true)
    List<MyInputOutputHistoryDto> getMyInputOutputHistory(
            String email,
            Integer offset, Integer limit,
            String dateFrom,
            String dateTo,
            Locale locale);

    List<Map<String, Object>> generateAndGetButtonsSet(InvoiceStatus status, InvoiceOperationPermission permittedOperation, boolean authorisedUserIsHolder, Locale locale);

}
