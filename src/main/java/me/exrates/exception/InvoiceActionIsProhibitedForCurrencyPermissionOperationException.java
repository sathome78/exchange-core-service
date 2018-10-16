package me.exrates.exception;

/**
 * Created by ValkSam
 */
public class InvoiceActionIsProhibitedForCurrencyPermissionOperationException extends RuntimeException {
    public InvoiceActionIsProhibitedForCurrencyPermissionOperationException(String message) {
        super(message);
    }
}
