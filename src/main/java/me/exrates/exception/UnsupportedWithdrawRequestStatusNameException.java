package me.exrates.exception;

public class UnsupportedWithdrawRequestStatusNameException extends RuntimeException {
    public UnsupportedWithdrawRequestStatusNameException(String name) {
        super(name);
    }
}
