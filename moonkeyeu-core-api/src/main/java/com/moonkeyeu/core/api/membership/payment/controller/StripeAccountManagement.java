package com.moonkeyeu.core.api.membership.payment.controller;

import com.moonkeyeu.core.api.membership.payment.dto.PaymentRequestDTO;
import com.moonkeyeu.core.api.membership.payment.service.StripeAccountService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeAccountManagement {

    private final StripeAccountService stripeAccountService;

    @PostMapping("/account/setting")
    public ResponseEntity<?> accountSettings(@RequestBody PaymentRequestDTO paymentRequest, @RequestHeader(value="Idempotency-Key") String idempotencyKey) throws StripeException {
        String response = stripeAccountService.stripeAccountSettings(paymentRequest, idempotencyKey);
        return ResponseEntity.ok(response);
    }
}
