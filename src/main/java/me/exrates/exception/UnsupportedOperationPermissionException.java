package me.exrates.exception;

/**
 * Created by OLEG on 28.02.2017.
 */
public class UnsupportedOperationPermissionException extends RuntimeException {

    public UnsupportedOperationPermissionException() {
    }

    public UnsupportedOperationPermissionException(String message) {
        super(message);
    }

    public UnsupportedOperationPermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedOperationPermissionException(Throwable cause) {
        super(cause);
    }
}
