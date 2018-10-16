package me.exrates.exception;

public class UnsupportedBusinessUserRoleNameException extends RuntimeException {
    public UnsupportedBusinessUserRoleNameException(String name) {
        super(name);
    }
}
