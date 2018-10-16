package me.exrates.exception;

public class InvoiceActionIsProhibitedForCurrentContextException extends RuntimeException {
    public InvoiceActionIsProhibitedForCurrentContextException(String name) {
        super(name);
    }
}
