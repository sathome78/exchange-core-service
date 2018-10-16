package me.exrates.exception;

public class MerchantException extends Exception {
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
