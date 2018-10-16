package me.exrates.exception;

public class UnsupportedTransactionSourceTypeIdException extends RuntimeException {

    public UnsupportedTransactionSourceTypeIdException(String message) {
        super(message);
    }
}