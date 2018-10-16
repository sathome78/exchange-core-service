package me.exrates.exception.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by maks on 03.07.2017.
 */
public class PinCodeCheckNeedException extends AuthenticationException {



    public PinCodeCheckNeedException(String msg, Throwable t) {
        super(msg, t);
    }

    public PinCodeCheckNeedException(String msg) {
        super(msg);
    }
}
