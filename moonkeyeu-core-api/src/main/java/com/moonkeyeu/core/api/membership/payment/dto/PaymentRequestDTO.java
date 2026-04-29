package com.moonkeyeu.core.api.membership.payment.dto;

import com.stripe.model.Product;

public record PaymentRequestDTO(String subscriptionId, String IdempotencyKey, String email, Long amount, String currency, Product product) { }
