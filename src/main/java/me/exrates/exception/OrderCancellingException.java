package me.exrates.exception;

public class OrderCancellingException extends RuntimeException {
    public OrderCancellingException(String message) {
        super(message);
    }
}
