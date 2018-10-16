package me.exrates.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by Valk on 07.04.16.
 */
public class NotVerifiedCaptchaError extends AuthenticationException {
    public NotVerifiedCaptchaError(String message) {
        super(message);
    }
}
