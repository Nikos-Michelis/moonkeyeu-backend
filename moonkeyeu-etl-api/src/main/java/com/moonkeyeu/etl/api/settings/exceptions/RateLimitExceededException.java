package com.moonkeyeu.etl.api.settings.exceptions;


import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {
    private long delay;

    public RateLimitExceededException(String message, long delay) {
        super(message);
        this.delay = delay;
    }
    public RateLimitExceededException(String message) {
        super(message);
    }

}
