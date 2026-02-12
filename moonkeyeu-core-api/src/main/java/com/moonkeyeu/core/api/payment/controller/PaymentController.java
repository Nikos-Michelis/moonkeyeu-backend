package com.moonkeyeu.core.api.payment.controller;

import com.moonkeyeu.core.api.payment.dto.PaymentRequestDTO;
import com.moonkeyeu.core.api.payment.dto.PaymentResponseDTO;
import com.moonkeyeu.core.api.payment.service.PaymentService;
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

    @PostMapping("/create-intent")
    public ResponseEntity<PaymentResponseDTO> createPayment(@RequestBody PaymentRequestDTO paymentRequest) {
        PaymentResponseDTO response = paymentService.createPayment(paymentRequest);
        return ResponseEntity.ok(response);
    }
}
