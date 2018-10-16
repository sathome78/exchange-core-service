package me.exrates.exception.security.exception;

/**
 * Created by OLEG on 23.08.2016.
 */
public class IncorrectPasswordException extends RuntimeException {

    public IncorrectPasswordException() {
    }

    public IncorrectPasswordException(String message) {
        super(message);
    }

    public IncorrectPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectPasswordException(Throwable cause) {
        super(cause);
    }
}
