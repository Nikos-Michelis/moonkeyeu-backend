package com.moonkeyeu.core.api.payment.dto;

import com.stripe.model.Product;

public record PaymentRequestDTO(String email, Long amount, String currency, Product[] product) { }
