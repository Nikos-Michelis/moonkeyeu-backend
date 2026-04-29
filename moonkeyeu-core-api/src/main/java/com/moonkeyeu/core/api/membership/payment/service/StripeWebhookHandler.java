package com.moonkeyeu.core.api.membership.payment.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;

public interface StripeWebhookHandler {
    void handleStripeEvent(Event event) throws StripeException;
}
