package com.moonkeyeu.core.api.subscription.service;

import com.moonkeyeu.core.api.subscription.model.Subscription;
import com.moonkeyeu.core.api.subscription.model.SubscriptionUsage;
import com.moonkeyeu.core.api.subscription.repository.SubscriptionUsageRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static java.time.ZoneOffset.UTC;

@Service
public class SubscriptionUsageService {

    private final SubscriptionUsageRepository subscriptionUsageRepository;

    public SubscriptionUsageService(SubscriptionUsageRepository subscriptionUsageRepository) {
        this.subscriptionUsageRepository = subscriptionUsageRepository;
    }

    public void useFromLimit(Subscription subscription, String expenseId, int usage) {
        SubscriptionUsage subscriptionUsageEntity = SubscriptionUsage.builder()
                .subscription(subscription)
                .expenseId(expenseId)
                .usageCount(usage)
                .usageDate(Instant.now())
                .build();
        subscriptionUsageRepository.save(subscriptionUsageEntity);
    }

    public long getUsageByInterval(Subscription subscription, Instant now) {
        Instant startDate = now.atZone(UTC).minusHours(subscription.getSubscriptionPlan().getTkResetHoursInterval()).toInstant();
        Long usage = subscriptionUsageRepository.sumUsageBySubscriptionAndDateRange(subscription, startDate, now);
        return (usage == null) ? 0 : usage;
    }
}