package me.exrates.exception;

public class TokenAccessDeniedException extends RuntimeException {
    public TokenAccessDeniedException(String access_to_token_is_forbidden) {
        super(access_to_token_is_forbidden);
    }
}
