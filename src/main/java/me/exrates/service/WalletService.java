package me.exrates.service;

import me.exrates.model.dto.*;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.main.CacheData;
import me.exrates.model.main.Wallet;
import me.exrates.model.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.vo.WalletOperationData;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public interface WalletService {
    WalletsForOrderCancelDto getWalletForStopOrderByStopOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType, int currencyPairId);

    @Transactional(propagation = Propagation.NESTED)
    BigDecimal getWalletABalance(int walletId);

    WalletTransferStatus walletBalanceChange(WalletOperationData walletOperationData);

    boolean ifEnoughMoney(int outWalletId, BigDecimal outAmount);

    WalletsForOrderAcceptionDto getWalletsForOrderByOrderIdAndBlock(int id, int userAcceptorId);

    int createNewWallet(Wallet wallet);

    List<OrderDetailDto> getOrderRelatedDataAndBlock(int orderId);

    List<MyWalletsStatisticsDto> getAllWalletsForUserReduced(CacheData cacheData, String email, Locale resolveLocale, CurrencyPairType type);

    List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(CacheData cacheData, String email, Locale english);

    int getWalletId(int userId, int id);

    @Transactional
    WalletsForOrderCancelDto getWalletForOrderByOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType);

    @Transactional
    WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId, String description);
}
