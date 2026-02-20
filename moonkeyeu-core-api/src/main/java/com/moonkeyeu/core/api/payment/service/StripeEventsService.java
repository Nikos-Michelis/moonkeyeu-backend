package com.moonkeyeu.core.api.payment.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;

public interface StripeEventsService {
    void captureStripeEvent(Event event) throws StripeException;
}
