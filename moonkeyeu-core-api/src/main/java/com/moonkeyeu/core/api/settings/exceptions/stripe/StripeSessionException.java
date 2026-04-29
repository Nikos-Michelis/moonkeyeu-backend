package com.moonkeyeu.core.api.settings.exceptions.stripe;

import org.springframework.http.HttpStatus;

public class StripeSessionException extends StripeException{
    public StripeSessionException(String title, String message, HttpStatus status) {
        super(title, message, status);
    }
}
