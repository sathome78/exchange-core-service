package me.exrates.exception;

/**
 * @author Vlad Dziubak
 * Date: 01.08.2018
 */
public class UserOperationAccessException extends RuntimeException{
    public UserOperationAccessException(String message) {
        super(message);
    }
}
