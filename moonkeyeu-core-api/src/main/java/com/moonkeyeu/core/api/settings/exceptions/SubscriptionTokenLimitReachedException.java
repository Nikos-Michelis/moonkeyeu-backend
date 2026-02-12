package com.moonkeyeu.core.api.settings.exceptions;


import org.springframework.http.HttpStatus;

public class SubscriptionTokenLimitReachedException extends SubscriptionException {

    public SubscriptionTokenLimitReachedException() {
        super("Token Limit Reached", "You have reached your token limit. Please try again next interval.",
                HttpStatus.TOO_MANY_REQUESTS);
    }
}