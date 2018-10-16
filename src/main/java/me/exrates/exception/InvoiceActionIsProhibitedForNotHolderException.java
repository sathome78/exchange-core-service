package me.exrates.exception;

public class InvoiceActionIsProhibitedForNotHolderException extends RuntimeException {
    public InvoiceActionIsProhibitedForNotHolderException(String string) {
        super(string);
    }
}
