package me.exrates.exception.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Vlad Dziubak
 * Date: 07.08.2018
 */
public class UnconfirmedUserException extends AuthenticationException {

    public UnconfirmedUserException(String msg, Throwable t) {
        super(msg, t);
    }

    public UnconfirmedUserException(String msg) {
        super(msg);
    }
}
