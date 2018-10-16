package me.exrates.exception;

public class MessageUndeliweredException extends RuntimeException {


    public MessageUndeliweredException() {
    }

    public MessageUndeliweredException(String message) {
        super(message);
    }

    public MessageUndeliweredException(String message, Throwable cause) {
        super(message, cause);
    }


}
