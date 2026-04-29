package com.moonkeyeu.core.api.membership.payment.service;


import com.moonkeyeu.core.api.membership.payment.dto.PaymentRequestDTO;
import com.stripe.exception.StripeException;

import java.util.List;
import java.util.Map;

public interface StripeSubscriptionService {
    String createSubscription(PaymentRequestDTO paymentRequest, String idempotencyKey) throws StripeException;
    String cancelSubscription(PaymentRequestDTO paymentRequest, String idempotencyKey) throws StripeException;
    String updateSubscription(PaymentRequestDTO paymentRequest, String idempotencyKey) throws StripeException;
    String renewSubscription(PaymentRequestDTO paymentRequest, String idempotencyKey) throws StripeException;
    List<Map<String, String>> findSubscriptionByCustomerEmail(String email) throws StripeException;
}
