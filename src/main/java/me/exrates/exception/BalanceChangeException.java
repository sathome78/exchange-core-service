package me.exrates.exception;

/**
 * Created by OLEG on 23.11.2016.
 */
public class BalanceChangeException extends RuntimeException {
    public BalanceChangeException() {
    }

    public BalanceChangeException(String message) {
        super(message);
    }

    public BalanceChangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BalanceChangeException(Throwable cause) {
        super(cause);
    }
}
