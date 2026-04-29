package com.moonkeyeu.core.api.membership.payment.service.impl.webhook;

import com.moonkeyeu.core.api.email.service.SubscriptionEmailService;
import com.moonkeyeu.core.api.membership.domain.model.*;
import com.moonkeyeu.core.api.membership.domain.repository.PlanRepository;
import com.moonkeyeu.core.api.membership.domain.repository.StripeCustomerRepository;
import com.moonkeyeu.core.api.membership.domain.repository.StripeInvoiceRepository;
import com.moonkeyeu.core.api.membership.payment.service.StripeInvoiceEventHandler;
import com.moonkeyeu.core.api.membership.subscription.service.SubscriptionService;
import com.moonkeyeu.core.api.settings.exceptions.stripe.CustomerNotFoundException;
import com.moonkeyeu.core.api.user.model.User;
import com.moonkeyeu.core.api.user.reporitory.UserRepository;
import com.stripe.model.Invoice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeInvoiceEventHandlerImpl implements StripeInvoiceEventHandler {
    private final StripeCustomerRepository stripeCustomerRepository;
    private final SubscriptionEmailService subscriptionEmailService;
    private final StripeInvoiceRepository stripeInvoiceRepository;
    private final SubscriptionService subscriptionService;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @Override
    public void handleUpcomingInvoice(Invoice invoice) {
        User user = userRepository.findByCustomerId(invoice.getCustomer())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        UserSubscription userSubscription = subscriptionService.getActiveUserSubscription(user.getStripeCustomer().getSubscriptions())
                .orElseThrow(() -> new ResourceNotFoundException("user subscriptions not found"));

        subscriptionEmailService.sendSubscriptionExpirationNotification(
                user.getEmail(),
                userSubscription
        );
    }

    @Transactional
    @Override
    public void handleInvoiceSucceeded(Invoice invoice) {
        System.out.println(invoice);
        String billingReason = invoice.getBillingReason();
        String customerEmail = invoice.getCustomer();
        String productId = invoice.getLines().getData().get(0).getPricing().getPriceDetails().getProduct();

        StripeCustomer stripeCustomer = stripeCustomerRepository.findStripeCustomerByStripeCustomerId(customerEmail)
                .orElseThrow(() -> new RuntimeException("No customer found with id: " + invoice.getCustomer()));

        UserSubscription userSubscription = subscriptionService.getActiveUserSubscription(stripeCustomer.getSubscriptions())
                .orElseThrow(() -> new RuntimeException("user subscriptions not found"));

        SubscriptionPlan subscriptionPlan = planRepository.findSubscriptionPlanByStripeProductId(productId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        StripeInvoice stripeInvoice = buildStripeInvoice(invoice, userSubscription, billingReason);
        stripeInvoiceRepository.save(stripeInvoice);
        //Optional<PaymentMethod> paymentMethod = paymentMethodRepository.
        System.out.println("billingReason --> " + billingReason);
        switch (billingReason) {
            case "subscription_create": {
                subscriptionEmailService.sendSubscriptionSuccessEmail(
                        customerEmail,
                        userSubscription,
                        invoice
                );
                break;
            }
            case "subscription_update": {
                subscriptionEmailService.sendSubscriptionUpdateEmail(
                        customerEmail,
                        userSubscription,
                        subscriptionPlan.getName()
                );
                break;
            }
            case "subscription_cycle": {
               /* subscriptionEmailService.sendSubscriptionRenewalEmail(
                        customerEmail,
                        userSubscription,
                        invoice
                );*/
                break;
            }
            default: {
                log.warn("Unhandled billing reason: {}", billingReason);
                break;
            }
        }
    }

    private StripeInvoice buildStripeInvoice(Invoice invoice, UserSubscription userSubscription, String billingReason) {
        return StripeInvoice.builder()
                .invoiceStripeId(invoice.getId())
                .subscription(userSubscription)
                .amountPaid(BigDecimal.valueOf(invoice.getAmountPaid()))
                .currency(Currency.valueOf(invoice.getCurrency().toUpperCase()))
                .status(InvoiceStatus.valueOf(invoice.getStatus().toUpperCase()))
                .billingReason(BillingReason.valueOf(billingReason.toUpperCase()))
                .hostedInvoiceUrl(invoice.getHostedInvoiceUrl())
                .invoiceCreatedAt(Instant.ofEpochSecond(invoice.getCreated()))
                .finalizedAt(Instant.ofEpochSecond(invoice.getStatusTransitions().getFinalizedAt()))
                .build();
    }
}
