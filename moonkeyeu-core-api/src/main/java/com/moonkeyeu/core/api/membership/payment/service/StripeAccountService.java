package com.moonkeyeu.core.api.membership.payment.service;

import com.moonkeyeu.core.api.membership.payment.dto.PaymentRequestDTO;
import com.stripe.exception.StripeException;

public interface StripeAccountService {
    String stripeAccountSettings(PaymentRequestDTO paymentRequest, String idempotencyKey) throws StripeException;
}
