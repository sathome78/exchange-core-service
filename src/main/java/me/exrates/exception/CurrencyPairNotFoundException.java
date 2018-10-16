package me.exrates.exception;

public class CurrencyPairNotFoundException extends RuntimeException {
    public CurrencyPairNotFoundException(String currency_pair_not_found) {
        super(currency_pair_not_found);
    }
}
