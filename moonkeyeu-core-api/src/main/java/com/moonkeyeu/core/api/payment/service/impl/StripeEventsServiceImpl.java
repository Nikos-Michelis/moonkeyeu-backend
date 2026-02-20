package com.moonkeyeu.core.api.payment.service.impl;

import com.moonkeyeu.core.api.payment.service.StripeEventsService;
import com.moonkeyeu.core.api.settings.exceptions.CustomerNotFoundException;
import com.moonkeyeu.core.api.subscription.dto.SubscriptionRequestDTO;
import com.moonkeyeu.core.api.subscription.model.SubscriptionPlan;
import com.moonkeyeu.core.api.subscription.model.SubscriptionStatus;
import com.moonkeyeu.core.api.subscription.repository.PlanRepository;
import com.moonkeyeu.core.api.subscription.repository.SubscriptionRepository;
import com.moonkeyeu.core.api.subscription.service.SubscriptionService;
import com.moonkeyeu.core.api.user.model.User;
import com.moonkeyeu.core.api.user.reporitory.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static java.time.ZoneOffset.UTC;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeEventsServiceImpl implements StripeEventsService {
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final PlanRepository planRepository;

    @Transactional
    @Override
    public void captureStripeEvent(Event event) throws StripeException {
        switch (event.getType()) {
            case "customer.subscription.created": {
                Subscription subscription = getSubscriptionFromEvent(event);

                String customerId = subscription.getCustomer();

                SubscriptionItem item = subscription.getItems().getData().get(0);
                Product product = getProductFromSubscriptionItem(item);

                String tierName = product.getName();
                String internalTierKey = product.getMetadata().get("app_tier");
                System.out.println(tierName);
                System.out.println(internalTierKey);

                User user = userRepository.findByCustomerId(customerId)
                        .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
                System.out.println(product.getId());
                SubscriptionPlan plan = planRepository.findSubscriptionPlanByStripeProductId(product.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

                SubscriptionRequestDTO subscriptionRequestDTO = SubscriptionRequestDTO.builder()
                        .productType(plan.getProductType())
                        .status(SubscriptionStatus.ACTIVE)
                        .purchasedAt(Instant.now())
                        .subscriptionExpireDate(Instant.now().atZone(UTC).plusDays(1).toInstant())
                        .build();

                subscriptionService.createOrUpdate(user, subscriptionRequestDTO);
               // userRepository.save(user);
                break;
            }

            case "customer.subscription.updated": {
                break;
            }

            default: {
                log.warn("Unhandled event type: {}", event.getType());
                break;
            }
        }
    }

    private Subscription getSubscriptionFromEvent(Event event) {
        return (Subscription) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
    }

    private Product getProductFromSubscriptionItem(SubscriptionItem subscriptionItem) throws StripeException {
        Price price = subscriptionItem.getPrice();
        String productId = price.getProduct();
        return Product.retrieve(productId);
    }
}
