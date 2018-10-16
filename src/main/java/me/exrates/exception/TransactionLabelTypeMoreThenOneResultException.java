package me.exrates.exception;

/**
 * Created by ValkSam
 */
public class TransactionLabelTypeMoreThenOneResultException extends RuntimeException {
    public TransactionLabelTypeMoreThenOneResultException(String message) {
        super(message);
    }
}
