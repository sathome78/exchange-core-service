package me.exrates.exception;

public class UnsupportedNotificationEventException extends RuntimeException {

    public UnsupportedNotificationEventException() {
    }

    public UnsupportedNotificationEventException(String message) {
        super(message);
    }

    public UnsupportedNotificationEventException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedNotificationEventException(Throwable cause) {
        super(cause);
    }
}
