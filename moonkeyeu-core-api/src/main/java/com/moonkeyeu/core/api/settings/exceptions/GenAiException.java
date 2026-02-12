package com.moonkeyeu.core.api.settings.exceptions;

public class GenAiException extends RuntimeException {
    public GenAiException(String message) {
        super(message);
    }
    public GenAiException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
