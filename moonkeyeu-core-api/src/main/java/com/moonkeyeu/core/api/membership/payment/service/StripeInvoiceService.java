package com.moonkeyeu.core.api.membership.payment.service;

import com.moonkeyeu.core.api.membership.payment.dto.PaymentRequestDTO;
import com.stripe.exception.StripeException;

import java.util.List;
import java.util.Map;

public interface StripeInvoiceService {
    List<Map<String, String>> getInvoices(PaymentRequestDTO requestDTO) throws StripeException;
}
