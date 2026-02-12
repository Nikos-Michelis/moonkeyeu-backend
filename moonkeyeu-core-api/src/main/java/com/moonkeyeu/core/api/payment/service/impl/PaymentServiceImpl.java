package com.moonkeyeu.core.api.payment.service.impl;

import com.moonkeyeu.core.api.payment.dto.PaymentRequestDTO;
import com.moonkeyeu.core.api.payment.dto.PaymentResponseDTO;
import com.moonkeyeu.core.api.payment.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Override
    public PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequest) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams
                    .builder()
                    .setAmount(paymentRequest.amount())
                    .setCurrency(paymentRequest.currency())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    ).build();
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            return new PaymentResponseDTO(paymentIntent.getClientSecret());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }


}
