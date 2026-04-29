package com.moonkeyeu.core.api.membership.subscription.service;

import com.moonkeyeu.core.api.membership.domain.model.UserSubscription;
import com.moonkeyeu.core.api.membership.domain.model.SubscriptionUsage;
import com.moonkeyeu.core.api.membership.domain.repository.SubscriptionUsageRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static java.time.ZoneOffset.UTC;

@Service
public class SubscriptionUsageService {

    private final SubscriptionUsageRepository subscriptionUsageRepository;

    public SubscriptionUsageService(SubscriptionUsageRepository subscriptionUsageRepository) {
        this.subscriptionUsageRepository = subscriptionUsageRepository;
    }

    public void useFromLimit(UserSubscription userSubscription, String expenseId, int usage) {
        SubscriptionUsage subscriptionUsageEntity = SubscriptionUsage.builder()
                .userSubscription(userSubscription)
                .expenseId(expenseId)
                .usageCount(usage)
                .usageDate(Instant.now())
                .build();
        subscriptionUsageRepository.save(subscriptionUsageEntity);
    }

    public long getUsageByInterval(UserSubscription userSubscription, Instant now) {
        Instant startDate = now.atZone(UTC).minusHours(userSubscription.getSubscriptionPlan().getTkResetHoursInterval()).toInstant();
        Long usage = subscriptionUsageRepository.sumUsageBySubscriptionAndDateRange(userSubscription, startDate, now);
        return (usage == null) ? 0 : usage;
    }
}