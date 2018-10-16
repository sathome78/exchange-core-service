package me.exrates.exception;

public class AuthorisedUserIsHolderParamNeededForThisActionException extends RuntimeException {
    public AuthorisedUserIsHolderParamNeededForThisActionException(String message) {
        super(message);
    }
}
