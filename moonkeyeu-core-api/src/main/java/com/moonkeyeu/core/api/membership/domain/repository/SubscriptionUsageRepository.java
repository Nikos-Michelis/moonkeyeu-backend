package com.moonkeyeu.core.api.membership.domain.repository;

import com.moonkeyeu.core.api.membership.subscription.dto.SubscriptionUsageDTO;
import com.moonkeyeu.core.api.membership.domain.model.SubscriptionUsage;
import com.moonkeyeu.core.api.membership.domain.model.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SubscriptionUsageRepository extends JpaRepository<SubscriptionUsage, Long> {

    @Query("""
         SELECT SUM(u.usageCount)
         FROM SubscriptionUsage u
         WHERE u.usageDate >= :startDate AND u.usageDate <= :endDate
         AND u.userSubscription = :userSubscription
    """)
    Long sumUsageBySubscriptionAndDateRange(
            @Param("subscription") UserSubscription userSubscription,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("""
    SELECT new com.moonkeyeu.core.api.membership.subscription.dto.SubscriptionUsageDTO(u.subscription_usage_id, u.usageDate)
        FROM SubscriptionUsage u
        INNER JOIN u.userSubscription s
        GROUP BY u.subscription_usage_id, u.usageDate, s.subscriptionPlan.tkResetHoursInterval
        HAVING MAX(u.usageDate) <= CURRENT_TIMESTAMP() - s.subscriptionPlan.tkResetHoursInterval MINUTE
    """)
    List<SubscriptionUsageDTO> findAllExpiredUsages();
}