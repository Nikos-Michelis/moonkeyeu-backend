package com.moonkeyeu.core.api.membership.payment.service.impl;

import com.moonkeyeu.core.api.membership.payment.dto.PaymentRequestDTO;
import com.moonkeyeu.core.api.membership.payment.service.StripeAccountService;
import com.moonkeyeu.core.api.membership.payment.util.CheckoutSessionUtil;
import com.moonkeyeu.core.api.membership.payment.util.CustomerUtil;
import com.moonkeyeu.core.api.membership.payment.util.PortalSessionUtil;
import com.moonkeyeu.core.api.settings.exceptions.stripe.CustomerNotFoundException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.net.RequestOptions;
import com.stripe.param.billingportal.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeAccountServiceImpl implements StripeAccountService {
    private final CustomerUtil customerUtil;
    private final PortalSessionUtil portalSessionUtil;
    private final CheckoutSessionUtil checkoutSessionUtil;
    @Value("${application.api.stripe.hosted.configs.account-management-id}")
    private String ACCOUNT_PORTAL_CONFIG;

    @Override
    public String stripeAccountSettings(PaymentRequestDTO paymentRequest, String idempotencyKey) throws StripeException {
        Customer stripeCustomer = customerUtil.findCustomerByEmail(paymentRequest.email())
                .orElseThrow(() -> new CustomerNotFoundException("Customer does not found with email: " + paymentRequest.email()));

        String stripeCustomerId = stripeCustomer.getId();

        SessionCreateParams.Builder paramsBuilder = portalSessionUtil.buildPortalSessionParams(stripeCustomerId);
        paramsBuilder.setConfiguration(ACCOUNT_PORTAL_CONFIG);

        RequestOptions requestOptions = checkoutSessionUtil.getIdempotencyKey(idempotencyKey);
        com.stripe.model.billingportal.Session session = portalSessionUtil.createPortalSession(paramsBuilder.build(), requestOptions);
        return session.getUrl();
    }
}
