package me.exrates.exception;

public class UnsupportedTransactionSourceTypeNameException extends RuntimeException {

    public UnsupportedTransactionSourceTypeNameException(String message) {
        super(message);
    }
}