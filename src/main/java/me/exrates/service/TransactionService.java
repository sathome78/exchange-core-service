package me.exrates.service;

import me.exrates.model.main.CacheData;
import me.exrates.model.main.Transaction;
import me.exrates.model.onlineTableDto.AccountStatementDto;

import java.util.List;
import java.util.Locale;

public interface TransactionService {

    boolean setStatusById(Integer trasactionId, Integer statusId);

    List<Transaction> getPayedRefTransactionsByOrderId(int orderId);

    List<AccountStatementDto> getAccountStatement(CacheData cacheData, Integer valueOf, Integer offset, Integer limit, Locale resolveLocale);

}
