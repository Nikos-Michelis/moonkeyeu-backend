package com.moonkeyeu.etl.api.settings.exceptions;

public class InvalidCleanupOperationException extends RuntimeException {
    public InvalidCleanupOperationException(String message) {
        super(message);
    }
}
