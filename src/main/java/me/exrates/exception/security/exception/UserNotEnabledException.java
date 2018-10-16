package me.exrates.exception.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by OLEG on 07.09.2016.
 */
public class UserNotEnabledException extends AuthenticationException {


    public UserNotEnabledException(String message) {
        super(message);
    }

    public UserNotEnabledException(String message, Throwable cause) {
        super(message, cause);
    }

}
