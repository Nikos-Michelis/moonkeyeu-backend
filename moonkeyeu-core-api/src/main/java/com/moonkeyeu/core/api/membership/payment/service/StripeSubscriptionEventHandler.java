package com.moonkeyeu.core.api.membership.payment.service;

import com.moonkeyeu.core.api.membership.domain.model.UserSubscription;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;

public interface StripeSubscriptionEventHandler {
    void handleSubscriptionChange(Subscription subscription) throws StripeException;
    UserSubscription handleSubscriptionCancellation(Subscription subscription);
}
