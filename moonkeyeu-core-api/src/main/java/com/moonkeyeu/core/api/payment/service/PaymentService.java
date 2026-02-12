package com.moonkeyeu.core.api.payment.service;

import com.moonkeyeu.core.api.payment.dto.PaymentRequestDTO;
import com.moonkeyeu.core.api.payment.dto.PaymentResponseDTO;

public interface PaymentService {
    PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequest);
}
