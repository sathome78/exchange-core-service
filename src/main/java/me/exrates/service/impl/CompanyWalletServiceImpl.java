package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.CompanyWalletDao;
import me.exrates.exception.NotEnoughUserWalletMoneyException;
import me.exrates.exception.WalletPersistException;
import me.exrates.model.main.CompanyWallet;
import me.exrates.model.main.Currency;
import me.exrates.service.CompanyWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static me.exrates.model.enums.ActionType.SUBTRACT;
import static me.exrates.util.BigDecimalProcessing.doAction;

@Service
@Log4j2
public class CompanyWalletServiceImpl implements CompanyWalletService {
    @Autowired
    private CompanyWalletDao companyWalletDao;

    public CompanyWallet create(Currency currency) {
        return companyWalletDao.create(currency);
    }

    @Transactional(readOnly = true)
    public CompanyWallet findByCurrency(Currency currency) {
        return companyWalletDao.findByCurrencyId(currency);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void deposit(CompanyWallet companyWallet, BigDecimal amount, BigDecimal commissionAmount) {
        final BigDecimal newBalance = companyWallet.getBalance().add(amount);
        final BigDecimal newCommissionBalance = companyWallet.getCommissionBalance().add(commissionAmount);
        companyWallet.setBalance(newBalance);
        companyWallet.setCommissionBalance(newCommissionBalance);
        if (!companyWalletDao.update(companyWallet)) {
            throw new WalletPersistException("Failed deposit on company wallet " + companyWallet.toString());
        }
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void withdraw(CompanyWallet companyWallet, BigDecimal amount, BigDecimal commissionAmount) {
        final BigDecimal newBalance = companyWallet.getBalance().subtract(amount);
        final BigDecimal newCommissionBalance = companyWallet.getCommissionBalance().add(commissionAmount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new NotEnoughUserWalletMoneyException("POTENTIAL HACKING! Not enough money on Company Account for operation!" + companyWallet.toString());
        }
        companyWallet.setBalance(newBalance);
        companyWallet.setCommissionBalance(newCommissionBalance);
        if (!companyWalletDao.update(companyWallet)) {
            throw new WalletPersistException("Failed withdraw on company wallet " + companyWallet.toString());
        }
    }

    @Override
    @Transactional
    public boolean substractCommissionBalanceById(Integer id, BigDecimal amount) {
        return companyWalletDao.substarctCommissionBalanceById(id, amount);
    }

    @Override
    public void withdrawReservedBalance(CompanyWallet companyWallet, BigDecimal amount) {
        BigDecimal newReservedBalance = doAction(companyWallet.getCommissionBalance(), amount, SUBTRACT);
        if (newReservedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new NotEnoughUserWalletMoneyException("POTENTIAL HACKING! Not enough money on Company Account for operation!" + companyWallet.toString());
        }
        companyWallet.setCommissionBalance(newReservedBalance);
        if (!companyWalletDao.update(companyWallet)) {
            throw new WalletPersistException("Failed withdraw on company wallet " + companyWallet.toString());
        }
    }
}
