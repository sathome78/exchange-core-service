package me.exrates.exception;

public class UnsupportedOrderTypeException extends RuntimeException {

    public UnsupportedOrderTypeException() {
    }

    public UnsupportedOrderTypeException(String message) {
        super(message);
    }

    public UnsupportedOrderTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedOrderTypeException(Throwable cause) {
        super(cause);
    }
}
