package me.exrates.exception.security.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidHmacSignatureException extends AuthenticationException {
    public InvalidHmacSignatureException(String msg, Throwable t) {
        super(msg, t);
    }

    public InvalidHmacSignatureException(String msg) {
        super(msg);
    }
}
