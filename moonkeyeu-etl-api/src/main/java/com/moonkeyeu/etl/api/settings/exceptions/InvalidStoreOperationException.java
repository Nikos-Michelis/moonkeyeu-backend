package com.moonkeyeu.etl.api.settings.exceptions;

public class InvalidStoreOperationException extends RuntimeException {
    public InvalidStoreOperationException(String message) {
        super(message);
    }
}
