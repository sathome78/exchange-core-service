package me.exrates.service.impl;

import me.exrates.dao.TransactionDao;
import me.exrates.model.User;
import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.TransactionType;
import me.exrates.model.main.*;
import me.exrates.model.onlineTableDto.AccountStatementDto;
import me.exrates.service.*;
import me.exrates.util.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.math.BigDecimal.ROUND_HALF_UP;

@Service
public class TransactionServiceImpl implements TransactionService {

  @Autowired
  private TransactionDao transactionDao;

  @Transactional
  public boolean setStatusById(Integer trasactionId, Integer statusId) {
    return transactionDao.setStatusById(trasactionId, statusId);
  }

  public List<Transaction> getPayedRefTransactionsByOrderId(int orderId) {
    return transactionDao.getPayedRefTransactionsByOrderId(orderId);
  }

  @Override
  public List<AccountStatementDto> getAccountStatement(CacheData cacheData, Integer walletId, Integer offset, Integer limit, Locale locale) {
    List<AccountStatementDto> result = transactionDao.getAccountStatement(walletId, offset, limit, locale);
    if (Cache.checkCache(cacheData, result)) {
      result = new ArrayList<AccountStatementDto>() {{
        add(new AccountStatementDto(false));
      }};
    }
    return result;
  }

}
