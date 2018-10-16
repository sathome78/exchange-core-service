package me.exrates.exception;

public class AlreadyAcceptedOrderException extends OrderAcceptionException {
    public AlreadyAcceptedOrderException(String message) {
        super(message);
    }
}
