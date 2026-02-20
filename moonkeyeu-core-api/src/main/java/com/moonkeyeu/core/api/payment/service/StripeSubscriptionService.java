package com.moonkeyeu.core.api.payment.service;


import com.moonkeyeu.core.api.payment.dto.PaymentRequestDTO;
import com.stripe.exception.StripeException;

import java.util.List;
import java.util.Map;

public interface StripeSubscriptionService {
    String createSubscription(PaymentRequestDTO paymentRequest) throws StripeException;
    String cancelSubscription(PaymentRequestDTO paymentRequest) throws StripeException;
    String upgradeSubscription(PaymentRequestDTO paymentRequest) throws StripeException;
    List<Map<String, String>> findSubscriptionByCustomerEmail(PaymentRequestDTO paymentRequest) throws StripeException;
}
