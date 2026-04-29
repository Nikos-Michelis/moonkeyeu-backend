package com.moonkeyeu.core.api.membership.subscription.service;

import com.moonkeyeu.core.api.membership.domain.model.SubscriptionStatus;
import com.moonkeyeu.core.api.settings.exceptions.subscription.SubscriptionNotFoundException;
import com.moonkeyeu.core.api.membership.domain.model.StripeCustomer;
import com.moonkeyeu.core.api.membership.domain.model.SubscriptionPlan;
import com.moonkeyeu.core.api.membership.payment.dto.StripeSubscriptionDTO;
import com.moonkeyeu.core.api.membership.domain.model.UserSubscription;
import com.moonkeyeu.core.api.membership.domain.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

@Service
public class SubscriptionService {
    private final EnumSet<SubscriptionStatus> ACTIVE_STATUSES =
            EnumSet.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING);
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public UserSubscription getSubscriptionByStripeCustomer(String stripeCustomerId) throws SubscriptionNotFoundException {
        return subscriptionRepository.findUserSubscriptionsByStripeCustomer_StripeCustomerId(stripeCustomerId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for the provided user "  + stripeCustomerId));
    }

    public Optional<UserSubscription> getActiveUserSubscription(Set<UserSubscription> userSubscriptions) {
        return userSubscriptions.stream()
                .filter(s -> ACTIVE_STATUSES.contains(SubscriptionStatus.valueOf(s.getStatus().name())))
                .findFirst();
    }

    @Transactional
    public UserSubscription createOrUpdate(StripeCustomer stripeCustomer, StripeSubscriptionDTO stripeSubscriptionDTO, SubscriptionPlan plan) {
       UserSubscription userSubscription = subscriptionRepository.findUserSubscriptionsByStripeCustomer_StripeCustomerId(stripeCustomer.getStripeCustomerId())
                .orElseGet(() -> UserSubscription.builder().stripeCustomer(stripeCustomer).build());

        userSubscription.setSubscriptionPlan(plan);
        userSubscription.setStatus(SubscriptionStatus.valueOf(stripeSubscriptionDTO.getStatus().name().toUpperCase()));
        userSubscription.setStripeSubscriptionId(stripeSubscriptionDTO.getStripeSubscriptionId());
        userSubscription.setCurrentPeriodStart(stripeSubscriptionDTO.getCurrentPeriodStart());
        userSubscription.setCurrentPeriodEnd(stripeSubscriptionDTO.getCurrentPeriodEnd());
        //subscription.getPlan().setTkResetHoursInterval(request.getTokenResetMinutesInterval());
        subscriptionRepository.save(userSubscription);
        return userSubscription;
    }

    private UserSubscription getUserSubscription(StripeCustomer stripeCustomer, StripeSubscriptionDTO stripeSubscriptionDTO, SubscriptionPlan plan) {
        return UserSubscription.builder()
                        .stripeCustomer(stripeCustomer)
                        .subscriptionPlan(plan)
                        .status(SubscriptionStatus.valueOf(stripeSubscriptionDTO.getStatus().name().toUpperCase()))
                        .stripeSubscriptionId(stripeSubscriptionDTO.getStripeSubscriptionId())
                        .currentPeriodStart(stripeSubscriptionDTO.getCurrentPeriodStart())
                        .currentPeriodEnd(stripeSubscriptionDTO.getCurrentPeriodEnd())
                        .build();
    }

}