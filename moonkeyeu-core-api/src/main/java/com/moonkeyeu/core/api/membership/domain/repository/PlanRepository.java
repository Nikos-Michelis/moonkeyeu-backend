package com.moonkeyeu.core.api.membership.domain.repository;

import com.moonkeyeu.core.api.membership.domain.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<SubscriptionPlan, Integer> {
    Optional<SubscriptionPlan> findSubscriptionPlanByStripeProductId(String id);
}
