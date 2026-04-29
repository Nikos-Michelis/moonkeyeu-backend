package com.moonkeyeu.core.api.membership.subscription.scheduled;

import com.moonkeyeu.core.api.membership.subscription.dto.SubscriptionUsageDTO;
import com.moonkeyeu.core.api.membership.domain.repository.SubscriptionUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledUsageTokenCleanup {
    private final SubscriptionUsageRepository subscriptionUsageRepository;
    private final int USAGE_THRESHOLD = 6;

    //@Scheduled(fixedRate = 1000)
    @Scheduled(cron = "0 0/30 * * * *")
    public void cleanupUsageTokens() {
        List<SubscriptionUsageDTO> subscriptionUsage = subscriptionUsageRepository.findAllExpiredUsages();
        for (SubscriptionUsageDTO subscriptionUsageDTO : subscriptionUsage) {
            subscriptionUsageRepository.deleteById(subscriptionUsageDTO.subscription_usage_id());
        }
    }
}
