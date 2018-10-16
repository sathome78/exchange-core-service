package me.exrates.exception;

public class UnrecognisedUrlPathForNewsTypeException extends RuntimeException {
    public UnrecognisedUrlPathForNewsTypeException(String name) {
        super(name);
    }
}
