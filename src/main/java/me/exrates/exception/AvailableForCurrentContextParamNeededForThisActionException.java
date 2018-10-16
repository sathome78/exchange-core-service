package me.exrates.exception;

/**
 * Created by ValkSam
 */
public class AvailableForCurrentContextParamNeededForThisActionException extends RuntimeException {
    public AvailableForCurrentContextParamNeededForThisActionException(String message) {
        super(message);
    }
}
