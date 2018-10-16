package me.exrates.exception;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class UnsupportedOperationTypeException extends RuntimeException {

    public UnsupportedOperationTypeException(int tupleId) {
        super("No such operation type " + tupleId);
    }

    public UnsupportedOperationTypeException(String message) {
        super(message);
    }
}