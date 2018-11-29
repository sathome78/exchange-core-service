package me.exrates.controller.ngcontroller.service.impl;

import me.exrates.controller.ngcontroller.service.NgWalletService;
import me.exrates.dao.WalletDao;
import me.exrates.model.enums.InvoiceStatus;
import me.exrates.model.enums.WithdrawStatusEnum;
import me.exrates.model.onlineTableDto.MyWalletsDetailedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

import static java.util.stream.Collectors.toList;

@Service
public class NgWalletServiceImpl implements NgWalletService {

    private final WalletDao walletDao;

    @Autowired
    public NgWalletServiceImpl(WalletDao walletDao) {
        this.walletDao = walletDao;
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, Locale locale) {
        List<Integer> withdrawStatusIdForWhichMoneyIsReserved = WithdrawStatusEnum.getEndStatesSet().stream().map(InvoiceStatus::getCode).collect(toList());
        return walletDao.getAllWalletsForUserDetailed(email, withdrawStatusIdForWhichMoneyIsReserved, locale);
    }
}
