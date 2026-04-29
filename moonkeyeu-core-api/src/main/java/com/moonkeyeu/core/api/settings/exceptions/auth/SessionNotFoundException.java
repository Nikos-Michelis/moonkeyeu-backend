package com.moonkeyeu.core.api.settings.exceptions.auth;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(String message) {
        super(message);
    }
}
