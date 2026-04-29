package com.moonkeyeu.core.api.membership.domain.repository;

import com.moonkeyeu.core.api.membership.domain.model.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    Optional<UserSubscription> findUserSubscriptionsByStripeCustomer_StripeCustomerId(String StripeCustomerId);
    @Modifying
    @Query(
        """
            DELETE UserSubscription us WHERE us.subscriptionId = :subscriptionId
        """
    )
    void deleteSubscriptionBySubscription_id(@Param("subscriptionId") Long subscriptionId);
}