package me.exrates.exception;

public class OrderDeletingException extends RuntimeException{
    public OrderDeletingException(String message) {
        super(message);
    }
}
