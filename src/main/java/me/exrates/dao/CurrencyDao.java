package me.exrates.dao;


import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.main.Currency;
import me.exrates.model.main.CurrencyPair;

import java.util.List;

public interface CurrencyDao {

    CurrencyPair findCurrencyPairById(int currencyPairId);

    CurrencyPair getNotHiddenCurrencyPairByName(String currencyPair);

    List<CurrencyPair> getAllCurrencyPairs(CurrencyPairType type);

    CurrencyPair findCurrencyPairByName(String currencyPair);

    List<CurrencyPair> findPermitedCurrencyPairs(CurrencyPairType currencyPairType);

    List<Currency> findAllCurrenciesWithHidden();

    Currency findByName(String name);

    CurrencyPair findCurrencyPairByOrderId(Integer orderId);
}
