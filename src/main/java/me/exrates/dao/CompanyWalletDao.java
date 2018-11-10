package me.exrates.dao;

import me.exrates.model.main.CompanyWallet;
import me.exrates.model.main.Currency;

import java.math.BigDecimal;

public interface CompanyWalletDao {


    CompanyWallet create(Currency currency);

    CompanyWallet findByCurrencyId(Currency currency);

    boolean update(CompanyWallet companyWallet);

    boolean substarctCommissionBalanceById(Integer id, BigDecimal amount);
}
