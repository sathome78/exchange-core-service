package me.exrates.dao;

import me.exrates.model.main.Transaction;
import me.exrates.model.onlineTableDto.AccountStatementDto;

import java.util.List;
import java.util.Locale;

public interface TransactionDao {

    Transaction create(Transaction transaction);

    boolean delete(int id);

    boolean setStatusById(Integer trasactionId, Integer statusId);

    List<Transaction> getPayedRefTransactionsByOrderId(int orderId);

    List<AccountStatementDto> getAccountStatement(Integer walletId, Integer offset, Integer limit, Locale locale);

    boolean updateForProvided(Transaction transaction);
}
