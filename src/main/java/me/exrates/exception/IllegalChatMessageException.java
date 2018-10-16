package me.exrates.exception;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class IllegalChatMessageException extends Exception {

    public IllegalChatMessageException(String message) {
        super(message);
    }

    public IllegalChatMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
