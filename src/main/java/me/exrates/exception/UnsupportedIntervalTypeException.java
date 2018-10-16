package me.exrates.exception;

public class UnsupportedIntervalTypeException extends RuntimeException {

    public UnsupportedIntervalTypeException(String intervalType) {
        super("No such interval type " + intervalType);
    }
}