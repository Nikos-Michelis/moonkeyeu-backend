package com.moonkeyeu.core.api.subscription.repository;

import com.moonkeyeu.core.api.subscription.model.Subscription;
import com.moonkeyeu.core.api.subscription.model.SubscriptionUsage;
import com.moonkeyeu.core.api.subscription.dto.SubscriptionUsageDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface SubscriptionUsageRepository extends JpaRepository<SubscriptionUsage, Long> {

    @Query("""
         SELECT SUM(u.usageCount)
         FROM SubscriptionUsage u
         WHERE u.usageDate >= :startDate AND u.usageDate <= :endDate
         AND u.subscription = :subscription
    """)
    Long sumUsageBySubscriptionAndDateRange(
            @Param("subscription") Subscription subscription,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("""
    SELECT new com.moonkeyeu.core.api.subscription.dto.SubscriptionUsageDTO(u.subscription_usage_id, u.usageDate)
        FROM SubscriptionUsage u
        INNER JOIN u.subscription s
        GROUP BY u.subscription_usage_id, u.usageDate, s.tokenResetMinutesInterval
        HAVING MAX(u.usageDate) <= CURRENT_TIMESTAMP() - s.tokenResetMinutesInterval MINUTE
    """)
    List<SubscriptionUsageDTO> findAllExpiredUsages();
}