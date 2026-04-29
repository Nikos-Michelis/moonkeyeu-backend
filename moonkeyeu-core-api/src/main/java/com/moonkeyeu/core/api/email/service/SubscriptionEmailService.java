package com.moonkeyeu.core.api.email.service;

import com.moonkeyeu.core.api.membership.domain.model.UserSubscription;
import com.moonkeyeu.core.api.membership.payment.dto.email.SubscriptionRenewalContext;
import com.stripe.model.Invoice;

public interface SubscriptionEmailService {
    void sendSubscriptionSuccessEmail(String email, UserSubscription subscription, Invoice invoice);
    void sendSubscriptionUpdateEmail(String email, UserSubscription subscription, String previousPlanName);
    void sendSubscriptionExpirationNotification(String email, UserSubscription subscription);
    void sendSubscriptionCancelledEmail(String email, UserSubscription subscription);
    void sendSubscriptionRenewalEmail(SubscriptionRenewalContext context);
}
