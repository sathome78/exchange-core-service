package me.exrates.exception.security.exception;

import org.springframework.security.core.AuthenticationException;


public class Google2faCodeCheckNeedException extends AuthenticationException {


    public Google2faCodeCheckNeedException(String msg, Throwable t) {
        super(msg, t);
    }

    public Google2faCodeCheckNeedException(String msg) {
        super(msg);
    }
}
