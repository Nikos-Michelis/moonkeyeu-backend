package com.moonkeyeu.core.api.membership.payment.service.impl.webhook;

import com.moonkeyeu.core.api.membership.domain.model.SubscriptionPlan;
import com.moonkeyeu.core.api.membership.domain.model.SubscriptionStatus;
import com.moonkeyeu.core.api.membership.domain.model.UserSubscription;
import com.moonkeyeu.core.api.membership.domain.repository.PlanRepository;
import com.moonkeyeu.core.api.membership.payment.dto.StripeSubscriptionDTO;
import com.moonkeyeu.core.api.membership.payment.service.StripeSubscriptionEventHandler;
import com.moonkeyeu.core.api.membership.payment.util.ProductUtil;
import com.moonkeyeu.core.api.membership.subscription.service.SubscriptionService;
import com.moonkeyeu.core.api.settings.exceptions.stripe.CustomerNotFoundException;
import com.moonkeyeu.core.api.user.model.User;
import com.moonkeyeu.core.api.user.reporitory.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.stripe.model.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class StripeSubscriptionEventHandlerImpl implements StripeSubscriptionEventHandler {
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final PlanRepository planRepository;
    private final ProductUtil productUtil;

    @Transactional
    public void handleSubscriptionChange(Subscription subscription) throws StripeException {
        String customerId = subscription.getCustomer();
        Product product = productUtil.getProductBySubscription(subscription);

        User user = userRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        SubscriptionPlan plan = planRepository.findSubscriptionPlanByStripeProductId(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        StripeSubscriptionDTO stripeSubscriptionDTO = StripeSubscriptionDTO.builder()
                .stripeSubscriptionId(subscription.getId())
                .status(SubscriptionStatus.valueOf(subscription.getStatus().toUpperCase()))
                .currentPeriodStart(Instant.ofEpochSecond(subscription.getItems().getData().get(0).getCurrentPeriodStart()))
                .currentPeriodEnd(Instant.ofEpochSecond(subscription.getItems().getData().get(0).getCurrentPeriodEnd()))
                .build();

        subscriptionService.createOrUpdate(user.getStripeCustomer(), stripeSubscriptionDTO, plan);
    }

    @Transactional
    public UserSubscription handleSubscriptionCancellation(Subscription subscription) {
        String customerId = subscription.getCustomer();
        User user = userRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        SubscriptionPlan plan = planRepository.findSubscriptionPlanByStripeProductId("prod_free")
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        boolean isCancelingAtPeriodEnd = subscription.getCancelAtPeriodEnd();

        SubscriptionStatus effectiveStatus = isCancelingAtPeriodEnd
                ? SubscriptionStatus.ACTIVE
                : SubscriptionStatus.valueOf(subscription.getStatus().toUpperCase());

        StripeSubscriptionDTO stripeSubscriptionDTO = StripeSubscriptionDTO.builder()
                .stripeSubscriptionId(subscription.getId())
                .status(effectiveStatus)
                .currentPeriodStart(Instant.ofEpochSecond(subscription.getItems().getData().get(0).getCurrentPeriodStart()))
                .currentPeriodEnd(Instant.ofEpochSecond(subscription.getItems().getData().get(0).getCurrentPeriodEnd()))
                .build();

        return subscriptionService.createOrUpdate(user.getStripeCustomer(), stripeSubscriptionDTO, plan);
    }
}
