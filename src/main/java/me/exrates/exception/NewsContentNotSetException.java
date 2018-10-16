package me.exrates.exception;

public class NewsContentNotSetException extends RuntimeException {
    public NewsContentNotSetException(String content_must_be_set) {
        super(content_must_be_set);
    }
}
