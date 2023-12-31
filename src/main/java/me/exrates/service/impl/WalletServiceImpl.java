package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.WalletDao;
import me.exrates.model.dto.OrderDetailDto;
import me.exrates.model.dto.WalletTransferStatus;
import me.exrates.model.dto.WalletsForOrderAcceptionDto;
import me.exrates.model.dto.WalletsForOrderCancelDto;
import me.exrates.model.enums.*;
import me.exrates.model.main.CacheData;
import me.exrates.model.main.CurrencyPair;
import me.exrates.model.main.Wallet;
import me.exrates.model.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.WalletService;
import me.exrates.util.BigDecimalProcessing;
import me.exrates.util.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Service
@Log4j2
public class WalletServiceImpl implements WalletService {


    @Autowired
    private WalletDao walletDao;
    @Autowired
    private CurrencyServiceImpl currencyServiceImpl;

    @Transactional( readOnly = true)
//    @Transactional(transactionManager = "slaveTxManager", readOnly = true) //TODO
    public List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(CacheData cacheData,
                                                                   String email, Locale locale) {
        List<Integer> withdrawStatusIdForWhichMoneyIsReserved = WithdrawStatusEnum.getEndStatesSet().stream().map(InvoiceStatus::getCode).collect(Collectors.toList());
        List<MyWalletsDetailedDto> result = walletDao.getAllWalletsForUserDetailed(email, withdrawStatusIdForWhichMoneyIsReserved, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<MyWalletsDetailedDto>() {{
                add(new MyWalletsDetailedDto(false));
            }};
        }
        return result;
    }

    @Transactional(readOnly = true)
//    @Transactional(transactionManager = "slaveTxManager", readOnly = true) //TODO
    public List<MyWalletsStatisticsDto> getAllWalletsForUserReduced(CacheData cacheData, String email, Locale locale, CurrencyPairType type) {
        List<CurrencyPair> pairList = currencyServiceImpl.getAllCurrencyPairs(type);
        Set<Integer> currencies = pairList.stream().map(p -> p.getCurrency2().getId()).collect(Collectors.toSet());
        currencies.addAll(pairList.stream().map(p -> p.getCurrency1().getId()).collect(toSet()));
        return walletDao.getAllWalletsForUserAndCurrenciesReduced(email, locale, currencies);
    }

    public int getWalletId(int userId, int currencyId) {
        return walletDao.getWalletId(userId, currencyId);
    }

    @Transactional(readOnly = true)
    public boolean ifEnoughMoney(int walletId, BigDecimal amountForCheck) {
        BigDecimal balance = getWalletABalance(walletId);
        boolean result = balance.compareTo(amountForCheck) >= 0;
        if (!result) {
            log.error(String.format("Not enough wallet money: wallet id %s, actual amount %s but needed %s", walletId,
                    BigDecimalProcessing.formatNonePoint(balance, false),
                    BigDecimalProcessing.formatNonePoint(amountForCheck, false)));
        }
        return result;
    }

    public WalletsForOrderAcceptionDto getWalletsForOrderByOrderIdAndBlock(int orderId, int userAcceptorId) {
        return walletDao.getWalletsForOrderByOrderIdAndBlock(orderId, userAcceptorId);
    }

    @Transactional(propagation = Propagation.NESTED)
    public int createNewWallet(Wallet wallet) {
        return walletDao.createNewWallet(wallet);
    }


    public WalletTransferStatus walletBalanceChange(final WalletOperationData walletOperationData) {
        return walletDao.walletBalanceChange(walletOperationData);
    }

    @Transactional
    public List<OrderDetailDto> getOrderRelatedDataAndBlock(int orderId) {
        return walletDao.getOrderRelatedDataAndBlock(orderId);
    }


    @Transactional(propagation = Propagation.NESTED)
    public BigDecimal getWalletABalance(int walletId) {
        return walletDao.getWalletABalance(walletId);
    }

    @Transactional
    public WalletsForOrderCancelDto getWalletForOrderByOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType) {
        return walletDao.getWalletForOrderByOrderIdAndOperationTypeAndBlock(orderId, operationType);
    }

    @Transactional
    public WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId, String description) {
        return walletDao.walletInnerTransfer(walletId, amount, sourceType, sourceId, description);
    }

    @Override
    @Transactional
    public WalletsForOrderCancelDto getWalletForStopOrderByStopOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType, int currencyPairId) {
        return walletDao.getWalletForStopOrderByStopOrderIdAndOperationTypeAndBlock(orderId, operationType, currencyPairId);
    }

}
