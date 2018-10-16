package me.exrates.exception;

public class IncorrectCurrentUserException extends RuntimeException {

    public IncorrectCurrentUserException(String message) {
        super(message);
    }
}
