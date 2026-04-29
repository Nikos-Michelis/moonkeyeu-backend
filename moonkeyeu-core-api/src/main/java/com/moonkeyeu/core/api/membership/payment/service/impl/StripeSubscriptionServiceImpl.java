package com.moonkeyeu.core.api.membership.payment.service.impl;

import com.moonkeyeu.core.api.membership.domain.model.*;
import com.moonkeyeu.core.api.membership.domain.repository.IdempotencyKeyRepository;
import com.moonkeyeu.core.api.membership.domain.repository.PlanRepository;
import com.moonkeyeu.core.api.membership.payment.dto.PaymentRequestDTO;
import com.moonkeyeu.core.api.membership.payment.service.StripeSubscriptionService;
import com.moonkeyeu.core.api.membership.payment.util.*;
import com.moonkeyeu.core.api.settings.exceptions.stripe.CustomerNotFoundException;
import com.moonkeyeu.core.api.settings.exceptions.subscription.SubscriptionConflictException;
import com.moonkeyeu.core.api.settings.exceptions.subscription.SubscriptionNotFoundException;
import com.moonkeyeu.core.api.user.model.User;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.SubscriptionItemListParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.billingportal.SessionCreateParams.*;
import com.stripe.param.billingportal.SessionCreateParams.FlowData.SubscriptionUpdateConfirm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeSubscriptionServiceImpl implements StripeSubscriptionService {
    private final SubscriptionItemUtil subscriptionItemUtil;
    private final CheckoutSessionUtil checkoutSessionUtil;
    private final PortalSessionUtil portalSessionUtil;
    private final UserDetailsService userDetailsService;
    private final CustomerUtil customerUtil;
    private final ProductUtil productUtil;
    private final PlanRepository planRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    @Value("${application.api.stripe.tier.trial.period}")
    private Long trialPeriod;
    @Value("${application.api.stripe.hosted.configs.update-id}")
    private String UPDATE_PORTAL_CONFIG;
    @Value("${application.api.stripe.hosted.configs.cancel-id}")
    private String CANCEL_PORTAL_CONFIG;

    /**
     * Creates a Stripe Checkout Session for a subscription.
     * Checks for existing active subscriptions to prevent duplicates.
     * @param paymentRequest Contains email, subscriptionId, amount etc
     * @return String The URL to redirect the user to Stripe Checkout
     * @throws StripeException if Stripe API fails
     * @throws CustomerNotFoundException if the user already has an active subscription
     */
    @Override
    public String renewSubscription(PaymentRequestDTO paymentRequest, String idempotencyKey) throws StripeException {
        StripeCustomer stripeCustomer = customerUtil.findOrCreateStripeCustomer(paymentRequest.email());

        Subscription subscription = subscriptionItemUtil.getSubscriptionByStatusAndCustomer(stripeCustomer.getStripeCustomerId(), EnumSet.of(SubscriptionStatus.PAST_DUE, SubscriptionStatus.UNPAID))
               .orElseThrow(() -> new SubscriptionNotFoundException("User " + stripeCustomer.getStripeCustomerId() + " does not have any expired subscription."));

        RequestOptions requestOptions = checkoutSessionUtil.getIdempotencyKey(idempotencyKey);
        Invoice latestInvoice = Invoice.retrieve(subscription.getLatestInvoice(), requestOptions);

        return latestInvoice.getHostedInvoiceUrl();
    }

    /**
     * Creates a Stripe Checkout Session for a subscription.
     * Checks for existing active subscriptions to prevent duplicates.
     *
     * @param paymentRequest Contains email, subscriptionId, amount etc
     * @return String The URL to redirect the user to Stripe Checkout
     * @throws StripeException           if Stripe API fails
     * @throws CustomerNotFoundException if the user already has an active subscription
     */
    @Override
    @Transactional
    public String createSubscription(PaymentRequestDTO paymentRequest, String idempotencyKey) throws StripeException {

        Optional<IdempotencyKey> idempotency = idempotencyKeyRepository.findIdempotencyKeyByIdempotencyKey(idempotencyKey) ;
        if (idempotency.isPresent()) {
            return idempotency.get().getPayment().getSessionUrl();
        }
        StripeCustomer stripeCustomer = customerUtil.findOrCreateStripeCustomer(paymentRequest.email());
        String stripeCustomerId = stripeCustomer.getStripeCustomerId();

        SubscriptionPlan subscriptionPlan = planRepository.findSubscriptionPlanByStripeProductId(paymentRequest.product().getId())
                .orElseThrow(() -> new SubscriptionNotFoundException("Not found any plan with the provided id " +  paymentRequest.product().getId()));

        Optional<Subscription> subscription =
                subscriptionItemUtil.getSubscriptionByStatusAndCustomer(
                        stripeCustomer.getStripeCustomerId(),
                        EnumSet.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING)
                );

        if (subscription.isPresent()) {
            throw new SubscriptionConflictException("User already has an active subscription.");
        }

        RequestOptions requestOptions = checkoutSessionUtil.getIdempotencyKey(idempotencyKey);

        SessionCreateParams.Builder paramsBuilder = checkoutSessionUtil.buildCheckoutSession(
                SessionCreateParams.Mode.SUBSCRIPTION,
                stripeCustomerId
        );

        Product product = productUtil.buildStripeProduct(subscriptionPlan);
        SessionCreateParams.LineItem lineItem = subscriptionItemUtil.buildSubscriptionLineItem(product);
        paramsBuilder.addLineItem(lineItem);

        Session session = Session.create(paramsBuilder.build(), requestOptions);

        return session.getUrl();
    }

    @Override
    public String updateSubscription(PaymentRequestDTO paymentRequest, String idempotencyKey) throws StripeException {
        StripeCustomer stripeCustomer = customerUtil.findOrCreateStripeCustomer(paymentRequest.email());
        String stripeCustomerId = stripeCustomer.getStripeCustomerId();

        SubscriptionPlan subscriptionPlan = planRepository.findSubscriptionPlanByStripeProductId(paymentRequest.product().getId())
                .orElseThrow(() -> new SubscriptionNotFoundException("Not found any plan with the provided id " +  paymentRequest.product().getId()));

        Subscription subscription = Subscription.retrieve(paymentRequest.subscriptionId());
        String subscriptionItemId = subscription.getItems().getData().get(0).getId();

        SubscriptionUpdateConfirm subscriptionUpdateConfirm =
                portalSessionUtil.getSubscriptionUpdateConfirm(paymentRequest.subscriptionId(), subscriptionItemId, subscriptionPlan.getStripePriceId());
        Builder paramsBuilder = portalSessionUtil.buildPortalSessionParams(stripeCustomerId);
        paramsBuilder.setConfiguration(UPDATE_PORTAL_CONFIG);
        paramsBuilder.setFlowData(
                portalSessionUtil.buildPortalSessionFlow(FlowData.Type.SUBSCRIPTION_UPDATE_CONFIRM, "http://localhost:3000/billing/success")
                        .setSubscriptionUpdateConfirm(subscriptionUpdateConfirm)
                        .build()
        );

        RequestOptions requestOptions = checkoutSessionUtil.getIdempotencyKey(idempotencyKey);
        com.stripe.model.billingportal.Session session = portalSessionUtil.createPortalSession(paramsBuilder.build(), requestOptions);
        return session.getUrl();
    }

    @Override
    public List<Map<String, String>> findSubscriptionByCustomerEmail(String email) throws StripeException {
        User user = (User) userDetailsService.loadUserByUsername(email);
        String stripeCustomerId = user.getStripeCustomer().getStripeCustomerId();

        Subscription subscription =
                subscriptionItemUtil.getSubscriptionByStatusAndCustomer(stripeCustomerId, EnumSet.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING))
                        .orElseThrow(() -> new SubscriptionConflictException("User does not have any active subscription."));

        SubscriptionItemListParams subscriptionItemListParams = subscriptionItemUtil.getSubscriptionItemListParams(subscription);
        SubscriptionItemCollection subscriptionItemCollection = subscriptionItemUtil.getSubscriptionItemCollection(subscriptionItemListParams);

        List<Map<String, String>> response = new ArrayList<>();
        for (SubscriptionItem item : subscriptionItemCollection.getData()) {
            HashMap<String, String> subscriptionData = createSubscriptionResponse(item, subscription);
            response.add(subscriptionData);
        }

        return response;
    }

    @Override
    public String cancelSubscription(PaymentRequestDTO paymentRequest, String idempotencyKey) throws StripeException {
        StripeCustomer stripeCustomer = customerUtil.findOrCreateStripeCustomer(paymentRequest.email());
        String stripeCustomerId = stripeCustomer.getStripeCustomerId();

        FlowData.SubscriptionCancel SubscriptionCancel =
                portalSessionUtil.getSubscriptionCancelConfirm(paymentRequest.subscriptionId());

        Builder paramsBuilder = portalSessionUtil.buildPortalSessionParams(stripeCustomerId);
        paramsBuilder.setConfiguration(CANCEL_PORTAL_CONFIG);
        paramsBuilder.setFlowData(
                portalSessionUtil.buildPortalSessionFlow(FlowData.Type.SUBSCRIPTION_CANCEL, "http://localhost:3000/billing/cancel")
                        .setSubscriptionCancel(SubscriptionCancel)
                        .build()
        );

        RequestOptions requestOptions = checkoutSessionUtil.getIdempotencyKey(idempotencyKey);
        com.stripe.model.billingportal.Session session = portalSessionUtil.createPortalSession(paramsBuilder.build(), requestOptions);

        return session.getUrl();
    }

    private HashMap<String, String> createSubscriptionResponse(SubscriptionItem item, Subscription subscription) {
        HashMap<String, String> subscriptionData = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneOffset.UTC);
        String subscriptionPeriodEnd = formatter.format(Instant.ofEpochSecond(subscriptionItemUtil.getSubscriptionItemPeriodEnd(subscription)));
        String subscriptionStartDate = formatter.format(Instant.ofEpochSecond(subscription.getStartDate()));
        Product product = item.getPrice().getProductObject();

        subscriptionData.put("product", product.getId());
        subscriptionData.put("name", product.getName());
        subscriptionData.put("subscription", subscription.getId());
        subscriptionData.put("subscribedOn", subscriptionStartDate);
        subscriptionData.put("nextPaymentDate", subscriptionPeriodEnd);
        subscriptionData.put("price", item.getPrice().getUnitAmountDecimal().toString());

        return subscriptionData;
    }
}



