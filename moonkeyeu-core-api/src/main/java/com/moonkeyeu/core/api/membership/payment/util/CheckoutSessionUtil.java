package com.moonkeyeu.core.api.membership.payment.util;

import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.stripe.param.checkout.SessionCreateParams.SavedPaymentMethodOptions.AllowRedisplayFilter;

@Component
public class CheckoutSessionUtil {
    @Value("${application.frontend.url}")
    private String clientBaseURL;

    public SessionCreateParams.Builder buildCheckoutSession(SessionCreateParams.Mode mode, String customerId) {
        return SessionCreateParams.builder()
                .setMode(mode)
                .setCustomer(customerId)
                .setSavedPaymentMethodOptions(getPaymentMethodOptions())
                .setAutomaticTax(getCustomerTax())
                .setCustomerUpdate(getCustomerAddress())
                .setSuccessUrl(clientBaseURL + "success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(clientBaseURL + "failure");
    }

    public RequestOptions getIdempotencyKey(String idempotencyKey) {
        return RequestOptions.builder().setIdempotencyKey(idempotencyKey).build();
    }

    private SessionCreateParams.SavedPaymentMethodOptions getPaymentMethodOptions() {
        return SessionCreateParams.SavedPaymentMethodOptions.builder().addAllowRedisplayFilter(AllowRedisplayFilter.ALWAYS).build();
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
