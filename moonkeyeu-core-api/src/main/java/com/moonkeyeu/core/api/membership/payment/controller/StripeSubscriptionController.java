package com.moonkeyeu.core.api.membership.payment.controller;

import com.moonkeyeu.core.api.membership.payment.dto.PaymentRequestDTO;
import com.moonkeyeu.core.api.membership.payment.dto.PaymentResponseDTO;
import com.moonkeyeu.core.api.membership.payment.service.StripeSubscriptionService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stripe/payment")
@RequiredArgsConstructor
public class StripeSubscriptionController {
    private final StripeSubscriptionService stripeSubscriptionService;

   /* @Subscribed(
            expenseId = "create-subscription",
            products = {ProductType.FREE},
            rules = {
                    @SubscriptionRule(token = 0),
            }
    )*/
    @PostMapping("/subscription/new")
    public ResponseEntity<?> createSubscription(
            @RequestBody PaymentRequestDTO paymentRequest,
            @RequestHeader(value="Idempotency-Key") String idempotencyKey
    ) throws StripeException {
        String response = stripeSubscriptionService.createSubscription(paymentRequest, idempotencyKey);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/subscription/upgrade")
    public ResponseEntity<?> upgradeSubscription(
            @RequestBody PaymentRequestDTO paymentRequest,
            @RequestHeader(value="Idempotency-Key") String idempotencyKey
    ) throws StripeException {
        String response = stripeSubscriptionService.updateSubscription(paymentRequest, idempotencyKey);
        return ResponseEntity.ok(response);
    }

    /*@Subscribed(
            expenseId = "renew-subscription",
            products = {ProductType.BASIC, ProductType.PRO},
            rules = {
                    @SubscriptionRule(token = 0),
            }
    )*/
    @PostMapping("/subscription/renew")
    public ResponseEntity<?> renewSubscription(
            @RequestBody PaymentRequestDTO paymentRequest,
            @RequestHeader(value="Idempotency-Key") String idempotencyKey
    ) throws StripeException {
        String response = stripeSubscriptionService.renewSubscription(paymentRequest, idempotencyKey);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/subscription/list")
    public ResponseEntity<?> getSubscriptions(@RequestBody PaymentRequestDTO paymentRequest) throws StripeException {
        List<Map<String, String>> response = stripeSubscriptionService.findSubscriptionByCustomerEmail(paymentRequest.email());
        return ResponseEntity.ok(response);
    }

   /* @Subscribed(
            expenseId = "create-subscription",
            products = {ProductType.BASIC, ProductType.PRO},
            rules = {
                    @SubscriptionRule(token = 0),
            }
    )*/
    @PostMapping("/subscription/cancel")
    public ResponseEntity<?> cancelSubscription(
            @RequestBody PaymentRequestDTO paymentRequest,
            @RequestHeader(value="Idempotency-Key") String idempotencyKey
    ) throws StripeException {
        String response = stripeSubscriptionService.cancelSubscription(paymentRequest, idempotencyKey);
        return ResponseEntity.ok(response);
    }
}
