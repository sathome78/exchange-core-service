package me.exrates.exception.security.exception;

import org.springframework.security.core.AuthenticationException;

public class PinCodeCheckNeedException extends AuthenticationException {


    public PinCodeCheckNeedException(String msg, Throwable t) {
        super(msg, t);
    }

    public PinCodeCheckNeedException(String msg) {
        super(msg);
    }
}
