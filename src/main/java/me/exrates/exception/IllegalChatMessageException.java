package me.exrates.exception;

public class IllegalChatMessageException extends Exception {

    public IllegalChatMessageException(String message) {
        super(message);
    }

    public IllegalChatMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
