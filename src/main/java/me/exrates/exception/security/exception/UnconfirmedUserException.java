package me.exrates.exception.security.exception;

import org.springframework.security.core.AuthenticationException;

public class UnconfirmedUserException extends AuthenticationException {

    public UnconfirmedUserException(String msg, Throwable t) {
        super(msg, t);
    }

    public UnconfirmedUserException(String msg) {
        super(msg);
    }
}
