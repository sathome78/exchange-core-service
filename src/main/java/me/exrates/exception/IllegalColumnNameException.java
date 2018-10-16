package me.exrates.exception;

public class IllegalColumnNameException extends RuntimeException {
    public IllegalColumnNameException(String message) {
        super(message);
    }
}
