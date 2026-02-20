package com.moonkeyeu.core.api.payment.util;

import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CheckoutSessionUtil {
    @Value("${application.backend.url}")
    private String clientBaseURL;

    public SessionCreateParams.Builder buildCheckoutSession(SessionCreateParams.Mode mode, String customerId) {
        return SessionCreateParams.builder()
                .setMode(mode)
                .setCustomer(customerId)
                .setAutomaticTax(getCustomerTax())
                .setCustomerUpdate(getCustomerAddress())
                .setSuccessUrl(clientBaseURL + "success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(clientBaseURL + "failure");
    }

    private SessionCreateParams.AutomaticTax getCustomerTax() {
        return SessionCreateParams.AutomaticTax.builder().setEnabled(true).build();
    }

    private SessionCreateParams.CustomerUpdate getCustomerAddress() {
        return SessionCreateParams.CustomerUpdate.builder()
                .setAddress(SessionCreateParams.CustomerUpdate.Address.AUTO)
                .setName(SessionCreateParams.CustomerUpdate.Name.AUTO)
                .build();
    }
}
