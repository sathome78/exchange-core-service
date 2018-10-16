package me.exrates.exception;

public class BannedIpException extends RuntimeException {

    private long banDurationSeconds;

    public BannedIpException(String msg, long banDurationSeconds) {
        super(msg);
        this.banDurationSeconds = banDurationSeconds;
    }

    public long getBanDurationSeconds() {
        return banDurationSeconds;
    }
}
