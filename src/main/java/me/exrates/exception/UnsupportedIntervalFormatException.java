package me.exrates.exception;

public class UnsupportedIntervalFormatException extends RuntimeException {

    public UnsupportedIntervalFormatException(String intervalString) {
        super("No such interval format " + intervalString);
    }
}