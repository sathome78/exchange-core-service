package me.exrates.exception;

public class UnsupportedInvoiceStatusForActionException extends RuntimeException {
    public UnsupportedInvoiceStatusForActionException(String format) {
        super(format);
    }
}
