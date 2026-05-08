package com.moonkeyeu.etl.api.settings.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RemoteServiceException extends RuntimeException {
    private final HttpStatus status;

    public RemoteServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
