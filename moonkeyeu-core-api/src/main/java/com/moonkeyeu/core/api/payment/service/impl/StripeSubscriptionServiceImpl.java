package com.moonkeyeu.core.api.payment.service.impl;

import com.moonkeyeu.core.api.payment.dto.PaymentRequestDTO;
import com.moonkeyeu.core.api.payment.repository.ProductDAO;
import com.moonkeyeu.core.api.payment.service.StripeSubscriptionService;
import com.moonkeyeu.core.api.payment.util.CheckoutSessionUtil;
import com.moonkeyeu.core.api.payment.util.CustomerUtil;
import com.moonkeyeu.core.api.payment.util.SubscriptionItemUtil;
import com.moonkeyeu.core.api.settings.exceptions.CustomerNotFoundException;
import com.moonkeyeu.core.api.settings.exceptions.SubscriptionConflictException;
import com.moonkeyeu.core.api.subscription.service.SubscriptionService;
import com.moonkeyeu.core.api.user.model.User;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.SubscriptionItemListParams;
import com.stripe.param.SubscriptionListParams;
import com.stripe.param.SubscriptionUpdateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.Recurring.Interval;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

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
    private final UserDetailsService userDetailsService;
    private final SubscriptionService subscriptionService;
    private final CustomerUtil customerUtil;
    @Value("${application.api.stripe.tier.trial.period}")
    private Long trialPeriod;

    /**
     * Creates a Stripe Checkout Session for a subscription.
     * Checks for existing active subscriptions to prevent duplicates.
     * @param requestDTO Contains email, subscriptionId, amount etc
     * @return String The URL to redirect the user to Stripe Checkout
     * @throws StripeException if Stripe API fails
     * @throws CustomerNotFoundException if the user already has an active subscription
     */
    @Override
    public String createSubscription(PaymentRequestDTO requestDTO) throws StripeException {
        User user = customerUtil.findOrCreateStripeCustomer(requestDTO.email());
        SubscriptionListParams subscriptionListParams = subscriptionItemUtil.buildSubscriptionListParams(user.getCustomerId());
        SubscriptionCollection subscriptions = subscriptionItemUtil.getSubscriptionCollection(subscriptionListParams);
        List<Subscription> activeSubs = subscriptions.getData();

        if (!activeSubs.isEmpty()) {
            throw new SubscriptionConflictException("User already has an active subscription.");
        }

        SessionCreateParams.Builder paramsBuilder = checkoutSessionUtil.buildCheckoutSession(SessionCreateParams.Mode.SUBSCRIPTION, user.getCustomerId());
        if ("TRIAL".equalsIgnoreCase(requestDTO.product().getMetadata().get("app_tier"))) {
            paramsBuilder.setSubscriptionData(SessionCreateParams.SubscriptionData.builder().setTrialPeriodDays(trialPeriod).build());
        }

        Product dbProduct = ProductDAO.getProduct(requestDTO.product().getId());
        SessionCreateParams.LineItem lineItem = subscriptionItemUtil.buildSubscriptionLineItem(dbProduct, Interval.MONTH);
        paramsBuilder.addLineItem(lineItem);
        Session session = Session.create(paramsBuilder.build());
        return session.getUrl();
    }

    @Override
    public String upgradeSubscription(PaymentRequestDTO paymentRequest) throws StripeException {
        Subscription subscription = Subscription.retrieve(paymentRequest.subscriptionId());

        Product dbProduct = ProductDAO.getProduct(paymentRequest.product().getId());
        SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                .addItem(SubscriptionUpdateParams.Item.builder()
                        .setId(paymentRequest.subscriptionId())
                        .setPrice(dbProduct.getDefaultPriceObject().getId())
                        .build())
                .setTrialEnd(SubscriptionUpdateParams.TrialEnd.NOW)
                .setPaymentBehavior(SubscriptionUpdateParams.PaymentBehavior.PENDING_IF_INCOMPLETE)
                .setProrationBehavior(SubscriptionUpdateParams.ProrationBehavior.ALWAYS_INVOICE)
                .build();

        Subscription updatedSubscription = subscription.update(params);
        return updatedSubscription.getStatus();
    }

    @Override
    public List<Map<String, String>> findSubscriptionByCustomerEmail(PaymentRequestDTO paymentRequest) throws StripeException {
        User user = (User) userDetailsService.loadUserByUsername(paymentRequest.email());
        SubscriptionListParams subscriptionListParams = subscriptionItemUtil.buildSubscriptionListParams(user.getCustomerId());
        SubscriptionCollection subscriptions = subscriptionItemUtil.getSubscriptionCollection(subscriptionListParams);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneOffset.UTC);
        List<Map<String, String>> response = new ArrayList<>();
        for (Subscription subscription : subscriptions.getData()) {
            SubscriptionItemListParams subscriptionItemListParams = subscriptionItemUtil.buildSubscriptionItemListParams(subscription);
            SubscriptionItemCollection subscriptionItemCollection = subscriptionItemUtil.getSubscriptionItemCollection(subscriptionItemListParams);
            for (SubscriptionItem item : subscriptionItemCollection.getData()) {
                HashMap<String, String> subscriptionData = new HashMap<>();
                subscriptionData.put("appProductId", item.getPrice().getProductObject().getMetadata().get("app_id"));
                subscriptionData.put("tier", item.getPrice().getProductObject().getMetadata().get("app_tier"));
                subscriptionData.put("subscriptionId", subscription.getId());
                subscriptionData.put("subscribedOn", formatter.format(Instant.ofEpochSecond(subscription.getStartDate())));
                subscriptionData.put("nextPaymentDate", formatter.format(Instant.ofEpochSecond(subscription.getItems().getData().get(0).getCurrentPeriodEnd())));
                subscriptionData.put("price", item.getPrice().getUnitAmountDecimal().toString());
                if (subscription.getTrialEnd() != null && Instant.ofEpochSecond(subscription.getTrialEnd()).isAfter(Instant.now()))
                    subscriptionData.put("trialEndsOn", formatter.format(Instant.ofEpochSecond(subscription.getTrialEnd())));
                response.add(subscriptionData);
            }
        }
        return response;
    }

    @Override
    public String cancelSubscription(PaymentRequestDTO paymentRequest) throws StripeException {
        Subscription subscription = Subscription.retrieve(paymentRequest.subscriptionId());
        Subscription deletedSubscription = subscription.cancel();
        return deletedSubscription.getStatus();
    }
}



