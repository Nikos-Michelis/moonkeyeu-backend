package com.moonkeyeu.core.api.subscription.repository;

import com.moonkeyeu.core.api.subscription.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<SubscriptionPlan, Integer> {
    Optional<SubscriptionPlan> findSubscriptionPlanByStripeProductId(String id);
}
