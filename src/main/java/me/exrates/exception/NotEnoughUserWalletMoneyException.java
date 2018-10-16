package me.exrates.exception;

public class NotEnoughUserWalletMoneyException extends RuntimeException {
    public NotEnoughUserWalletMoneyException(String message) {
        super(message);
    }
}