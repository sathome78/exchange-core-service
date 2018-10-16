package me.exrates.exception;

public class UnsupportedInvoiceActionTypeNameException extends RuntimeException {
    public UnsupportedInvoiceActionTypeNameException(String message) {
        super(message);
    }
}
