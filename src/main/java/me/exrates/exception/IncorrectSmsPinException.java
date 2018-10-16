package me.exrates.exception;

/**
 * Created by Maks on 17.10.2017.
 */
public class IncorrectSmsPinException extends RuntimeException {

    public IncorrectSmsPinException() {
    }

    public IncorrectSmsPinException(String message) {
        super(message);
    }
}
