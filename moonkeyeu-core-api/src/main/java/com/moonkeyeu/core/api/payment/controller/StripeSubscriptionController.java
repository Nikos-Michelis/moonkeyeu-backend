package com.moonkeyeu.core.api.payment.controller;

import com.moonkeyeu.core.api.payment.dto.PaymentRequestDTO;
import com.moonkeyeu.core.api.payment.service.StripeSubscriptionService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class StripeSubscriptionController {
    private final StripeSubscriptionService stripeSubscriptionService;

    @PostMapping("/subscription/new")
    public ResponseEntity<?> createSubscription(@RequestBody PaymentRequestDTO paymentRequest) throws StripeException {
        String response = stripeSubscriptionService.createSubscription(paymentRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/subscription/upgrade")
    public ResponseEntity<?> upgradeSubscription(@RequestBody PaymentRequestDTO paymentRequest) throws StripeException {
        String response = stripeSubscriptionService.upgradeSubscription(paymentRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/subscription/list")
    public ResponseEntity<?> getSubscriptions(@RequestBody PaymentRequestDTO paymentRequest) throws StripeException {
        List<Map<String, String>> response = stripeSubscriptionService.findSubscriptionByCustomerEmail(paymentRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/subscription/cancel")
    public ResponseEntity<?> cancelSubscription(@RequestBody PaymentRequestDTO paymentRequest) throws StripeException {
        String response = stripeSubscriptionService.cancelSubscription(paymentRequest);
        return ResponseEntity.ok(response);
    }
}
