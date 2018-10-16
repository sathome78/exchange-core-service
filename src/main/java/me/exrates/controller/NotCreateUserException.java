package me.exrates.controller;

public class NotCreateUserException extends RuntimeException {
    public NotCreateUserException(String error_while_user_creation) {
        super(error_while_user_creation);
    }
}
