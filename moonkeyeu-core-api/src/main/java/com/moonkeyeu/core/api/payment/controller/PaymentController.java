package com.moonkeyeu.core.api.payment.controller;

import com.moonkeyeu.core.api.payment.dto.PaymentRequestDTO;
import com.moonkeyeu.core.api.payment.dto.PaymentResponseDTO;
import com.moonkeyeu.core.api.payment.service.PaymentService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/subscription/new")
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequestDTO paymentRequest) throws StripeException {
        String response = paymentService.createSubscription(paymentRequest);
        return ResponseEntity.ok(response);
    }
}
