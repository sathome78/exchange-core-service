package me.exrates.service;

import me.exrates.model.main.CompanyWallet;
import me.exrates.model.main.Currency;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface CompanyWalletService {
    CompanyWallet create(Currency currency);

    CompanyWallet findByCurrency(Currency currency);

    void deposit(CompanyWallet companyWallet, BigDecimal amount, BigDecimal commissionAmount);

    @Transactional(propagation = Propagation.NESTED)
    void withdraw(CompanyWallet companyWallet, BigDecimal amount, BigDecimal commissionAmount);

    boolean substractCommissionBalanceById(Integer id, BigDecimal amount);

    void withdrawReservedBalance(CompanyWallet companyWallet, BigDecimal amount);
}
