package com.moonkeyeu.etl.api.settings.exceptions;

public class S3StorageException extends RuntimeException {
    public S3StorageException(String message) {
        super(message);
    }
}
