package me.exrates.exception;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class UnsupportedOrderStatusException extends RuntimeException {

    public final int orderStatusId;

    public UnsupportedOrderStatusException(int tupleId) {
        super("No such order status " + tupleId);
        this.orderStatusId = tupleId;
    }
}