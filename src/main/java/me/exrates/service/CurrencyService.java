package me.exrates.service;

import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.main.Currency;
import me.exrates.model.main.CurrencyPair;

import java.util.List;

public interface CurrencyService {
    List<CurrencyPair> findAllCurrenciesBySecondPartName(String partName);

    CurrencyPair getNotHiddenCurrencyPairByName(String currencyPair);

    CurrencyPair findCurrencyPairById(int id);

    CurrencyPairLimitDto findLimitForRoleByCurrencyPairAndType(int id, OperationType operationType);

    List<CurrencyPair> getAllCurrencyPairs(CurrencyPairType type);

    CurrencyPair getCurrencyPairByName(String s);

    List<CurrencyPair> getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType cpType);

    List findAllCurrenciesWithHidden();

    List<CurrencyPair> findPermitedCurrencyPairs(CurrencyPairType ico);

    Currency findByName(String currencyNameForPay);

    List<CurrencyPair> getPairsByFirstPartName(String pathName);

    List<CurrencyPair> getPairsBySecondPartName(String pathName);
}
