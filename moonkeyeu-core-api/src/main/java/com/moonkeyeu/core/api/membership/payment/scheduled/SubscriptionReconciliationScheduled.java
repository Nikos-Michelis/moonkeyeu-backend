package com.moonkeyeu.core.api.membership.payment.scheduled;

import com.moonkeyeu.core.api.membership.domain.model.UserSubscription;
import com.moonkeyeu.core.api.membership.payment.dto.StripeSubscriptionDTO;
import com.moonkeyeu.core.api.membership.payment.service.StripeSubscriptionService;
import com.moonkeyeu.core.api.membership.payment.util.CustomerUtil;
import com.moonkeyeu.core.api.membership.payment.util.ProductUtil;
import com.moonkeyeu.core.api.membership.payment.util.SubscriptionItemUtil;
import com.moonkeyeu.core.api.membership.domain.model.SubscriptionPlan;
import com.moonkeyeu.core.api.membership.domain.model.SubscriptionStatus;
import com.moonkeyeu.core.api.membership.domain.repository.PlanRepository;
import com.moonkeyeu.core.api.membership.subscription.service.SubscriptionService;
import com.moonkeyeu.core.api.user.model.User;
import com.moonkeyeu.core.api.user.reporitory.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Product;
import com.stripe.model.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionReconciliationScheduled {
    private final StripeSubscriptionService stripeSubscriptionService;
    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final SubscriptionItemUtil subscriptionItemUtil;
    private final ProductUtil productUtil;
    private final CustomerUtil customerUtil;

    //@Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void validateUnsubscribedUsers() throws StripeException {
        List<User> unsubscribedUsers = userRepository.findAllUnsubscribedUsers();

        if (unsubscribedUsers.isEmpty()) {
            return;
        }

        Map<String, User> unsubscribedUserMap = getUsersMap(unsubscribedUsers);
        List<Subscription> subscriptions = subscriptionItemUtil.getLatestSubscriptionPerCustomer();
        for (Subscription subscription : subscriptions) {
            Optional<Customer> customer = customerUtil.findCustomerByCustomerId(subscription.getCustomer());

            if (customer.isEmpty()) {
                continue;
            }

            if (unsubscribedUserMap.containsKey(customer.get().getId())) {
                User user = unsubscribedUserMap.get(customer.get().getEmail());
                Product product = productUtil.getProductBySubscription(subscription);
                SubscriptionPlan plan = getSubscriptionPlanById(product.getId());
                StripeSubscriptionDTO stripeSubscriptionDTO = getStripeSubscriptionDTO(subscription);
                subscriptionService.createOrUpdate(user.getStripeCustomer(), stripeSubscriptionDTO, plan);
            }
        }
        log.info("Subscription validation for unsubscribed users completed");
    }

    //@Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void validateSubscribedUsers() throws StripeException {
        List<User> subscribedUsers = userRepository.findAllSubscribedUsers();

        if (subscribedUsers.isEmpty()) {
            return;
        }

        Map<String, User> subscribedUserMap = getUsersMap(subscribedUsers);
        List<Subscription> subscriptions = subscriptionItemUtil.getLatestSubscriptionPerCustomer();

        for (Subscription subscription : subscriptions) {

            Customer customer = Customer.retrieve(subscription.getCustomer());

            Product product = productUtil.getProductBySubscription(subscription);

            if (!subscribedUserMap.containsKey(customer.getEmail())) {
                continue;
            }

            User user = subscribedUserMap.get(customer.getEmail());

            UserSubscription userSubscription = subscriptionService.getActiveUserSubscription(user.getStripeCustomer().getSubscriptions())
                    .orElseThrow(() -> new ResourceNotFoundException("user subscriptions not found"));

            String currentStripePlanId = getCurrentPlan(userSubscription).getStripeProductId();

            if (currentStripePlanId.equals(product.getId())) {
                SubscriptionPlan plan = getSubscriptionPlanById(product.getId());
                StripeSubscriptionDTO stripeSubscriptionDTO = getStripeSubscriptionDTO(subscription);
                subscriptionService.createOrUpdate(user.getStripeCustomer(), stripeSubscriptionDTO, plan);
            }
        }


        log.info("Subscription validation for subscribed users completed");
    }

    private SubscriptionPlan getCurrentPlan(UserSubscription userSubscription) {
        return userSubscription.getSubscriptionPlan();
    }

    private Map<String, User> getUsersMap(List<User> users) {
        return users.stream().collect(Collectors.toMap(User::getEmail, user -> user));
    }

    private SubscriptionPlan getSubscriptionPlanById(String planId) {
        return planRepository.findSubscriptionPlanByStripeProductId(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
    }

    private StripeSubscriptionDTO getStripeSubscriptionDTO(Subscription subscription) {
        return StripeSubscriptionDTO.builder()
                .stripeSubscriptionId(subscription.getId())
                .status(SubscriptionStatus.valueOf(subscription.getStatus().toUpperCase()))
                .currentPeriodStart(Instant.ofEpochMilli(subscription.getStartDate()))
                .currentPeriodEnd(Instant.ofEpochMilli(subscription.getEndedAt()))
                .build();
    }
}
