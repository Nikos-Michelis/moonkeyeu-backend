package com.moonkeyeu.core.api.settings.exceptions.subscription;

import org.springframework.http.HttpStatus;

public class SubscriptionRequiredException extends SubscriptionException {

    public SubscriptionRequiredException() {
        super("Subscription Required", "Access to this feature requires an active subscription. Please subscribe and try again later",
                HttpStatus.PAYMENT_REQUIRED);
    }
}
