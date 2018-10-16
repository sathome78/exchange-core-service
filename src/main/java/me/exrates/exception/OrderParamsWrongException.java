package me.exrates.exception;

/**
 * Created by Valk on 04.04.16.
 */
public class OrderParamsWrongException extends RuntimeException {
    public OrderParamsWrongException() {
        super();
    }

    public OrderParamsWrongException(String message) {
        super(message);
    }
}
