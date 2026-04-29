package com.moonkeyeu.core.api.membership.payment.util;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.param.PaymentMethodListParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class PaymentMethodUtil {

    public void removePaymentMethod(String currentUserIdCustomerId, String paymentMethodId) throws StripeException {
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        String paymentMethodCustomerId = paymentMethod.getCustomer();
        if (paymentMethodCustomerId == null || !paymentMethodCustomerId.equals(currentUserIdCustomerId)) {
            log.error("Security Alert: User {} attempted to delete payment method {} owned by {}",
                    currentUserIdCustomerId, paymentMethodId, paymentMethodCustomerId);
            throw new SecurityException("You do not have permission to delete this payment method.");
        }
        paymentMethod.detach();
        log.info("Payment method {} detached successfully by user {}", paymentMethodId, currentUserIdCustomerId);
    }

    public List<PaymentMethod> getStripePaymentMethods(String stripeCustomerId) {
        try {
            PaymentMethodListParams params = PaymentMethodListParams.builder()
                    .setCustomer(stripeCustomerId)
                    .build();

            PaymentMethodCollection paymentMethods = PaymentMethod.list(params);
            return paymentMethods.getData();
        } catch (StripeException e) {
            log.error("Failed to fetch payment methods from Stripe for customer: {}", stripeCustomerId, e);
            throw new RuntimeException("Error communicating with Stripe", e);
        }
    }
}
