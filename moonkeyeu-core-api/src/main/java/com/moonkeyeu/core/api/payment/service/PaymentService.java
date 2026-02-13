package com.moonkeyeu.core.api.payment.service;

import com.moonkeyeu.core.api.payment.dto.PaymentRequestDTO;
import com.stripe.exception.StripeException;

public interface PaymentService {
    String createSubscription(PaymentRequestDTO paymentRequest) throws StripeException;
}
