package com.moonkeyeu.core.api.payment.service;

import com.moonkeyeu.core.api.payment.dto.PaymentRequestDTO;
import com.stripe.exception.StripeException;

import java.util.List;
import java.util.Map;

public interface StripeInvoicesService {
    List<Map<String, String>> getInvoices(PaymentRequestDTO requestDTO) throws StripeException;
}
