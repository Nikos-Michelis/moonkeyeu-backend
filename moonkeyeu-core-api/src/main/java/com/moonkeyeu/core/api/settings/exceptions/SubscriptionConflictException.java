package com.moonkeyeu.core.api.settings.exceptions;

import lombok.Getter;

@Getter
public class SubscriptionConflictException extends RuntimeException {
    private String errorCode;

    public SubscriptionConflictException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public SubscriptionConflictException(String message) {
        super(message);
    }

}