package me.exrates.exception;

public class WalletCreationException extends RuntimeException {
    public WalletCreationException(String message) {
        super(message);
    }
}