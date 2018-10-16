package me.exrates.exception;

/**
 * Created by Maks on 09.10.2017.
 */
public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException() {
    }

    public ServiceUnavailableException(String message) {
        super(message);
    }
}
