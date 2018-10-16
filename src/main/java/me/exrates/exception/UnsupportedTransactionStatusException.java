package me.exrates.exception;

public class UnsupportedTransactionStatusException extends RuntimeException {
    public UnsupportedTransactionStatusException(int transactionStatusId) {
        super("No such transaction status " + transactionStatusId);
    }
}