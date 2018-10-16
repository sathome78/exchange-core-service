package me.exrates.exception;

/**
 * Created by OLEG on 13.09.2016.
 */
public class AlreadyAcceptedOrderException extends OrderAcceptionException {
    public AlreadyAcceptedOrderException(String message) {
        super(message);
    }
}
