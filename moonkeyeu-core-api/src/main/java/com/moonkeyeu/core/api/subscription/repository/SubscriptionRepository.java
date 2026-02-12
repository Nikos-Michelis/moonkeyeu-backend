package com.moonkeyeu.core.api.subscription.repository;

import com.moonkeyeu.core.api.subscription.model.Subscription;
import com.moonkeyeu.core.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUser(User user);
}