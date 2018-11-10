package me.exrates.dao;

import me.exrates.model.dto.*;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.main.Wallet;
import me.exrates.model.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.vo.WalletOperationData;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public interface WalletDao {
    int getWalletId(int userId, int currencyId);

    List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, List<Integer> withdrawStatusIds, Locale locale);

    WalletsForOrderCancelDto getWalletForStopOrderByStopOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType, int currencyPairId);

    WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId, String description);

    WalletsForOrderCancelDto getWalletForOrderByOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType);

    WalletTransferStatus walletBalanceChange(WalletOperationData walletOperationData);

    BigDecimal getWalletABalance(int walletId);

    List<OrderDetailDto> getOrderRelatedDataAndBlock(int orderId);

    WalletsForOrderAcceptionDto getWalletsForOrderByOrderIdAndBlock(Integer orderId, Integer userAcceptorId);

    int createNewWallet(Wallet wallet);

    List<MyWalletsStatisticsDto> getAllWalletsForUserAndCurrenciesReduced(String email, Locale locale, Set<Integer> currencies);
}
