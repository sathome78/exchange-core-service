package me.exrates.controller.ngcontroller.service;

import me.exrates.model.onlineTableDto.MyWalletsDetailedDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

public interface NgWalletService {
    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, Locale locale);
}
