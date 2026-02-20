package com.moonkeyeu.core.api.subscription.service;

import com.moonkeyeu.core.api.subscription.dto.SubscriptionRequestDTO;
import com.moonkeyeu.core.api.subscription.model.Subscription;
import com.moonkeyeu.core.api.subscription.repository.SubscriptionRepository;
import com.moonkeyeu.core.api.user.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionService {

    private final UserDetailsService userDetailsService;
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(UserDetailsService userDetailsService, SubscriptionRepository subscriptionRepository) {
        this.userDetailsService = userDetailsService;
        this.subscriptionRepository = subscriptionRepository;
    }
    

    @Transactional
    public Subscription createOrUpdate(User user, SubscriptionRequestDTO request) {
        //User user = (User) userDetailsService.loadUserByUsername(userId);
        Subscription subscription = subscriptionRepository.findByUser(user)
                .orElseGet(() -> Subscription.builder()
                        .user(user)
                        .build());
        subscription.getSubscriptionPlan().setProductType(request.getProductType());
        subscription.setStatus(request.getStatus());
        //subscription.getPlan().setTkResetHoursInterval(request.getTokenResetMinutesInterval());
        subscription.setPurchasedAt(request.getPurchasedAt());
        subscription.setExpirationAt(request.getSubscriptionExpireDate());
        return subscriptionRepository.saveAndFlush(subscription);
    }
}