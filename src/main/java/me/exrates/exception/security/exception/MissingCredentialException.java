package me.exrates.exception.security.exception;

/**
 * Created by OLEG on 08.09.2016.
 */
public class MissingCredentialException extends RuntimeException {
    public MissingCredentialException() {
    }

    public MissingCredentialException(String message) {
        super(message);
    }

    public MissingCredentialException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingCredentialException(Throwable cause) {
        super(cause);
    }
}
