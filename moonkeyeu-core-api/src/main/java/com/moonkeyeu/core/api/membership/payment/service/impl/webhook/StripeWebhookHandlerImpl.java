package com.moonkeyeu.core.api.membership.payment.service.impl.webhook;

import com.moonkeyeu.core.api.membership.domain.model.*;
import com.moonkeyeu.core.api.membership.domain.repository.StripeCustomerRepository;
import com.moonkeyeu.core.api.membership.payment.service.StripeWebhookHandler;
import com.moonkeyeu.core.api.membership.payment.service.StripeInvoiceEventHandler;
import com.moonkeyeu.core.api.settings.exceptions.stripe.CustomerNotFoundException;
import com.moonkeyeu.core.api.email.service.SubscriptionEmailService;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeWebhookHandlerImpl implements StripeWebhookHandler {
    private final StripeCustomerRepository stripeCustomerRepository;
    private final StripeSubscriptionEventHandlerImpl stripeSubscriptionEventHandlerImpl;
    private final StripeInvoiceEventHandler stripeInvoiceEventHandler;
    private final SubscriptionEmailService subscriptionEmailService;

    @Transactional
    @Override
    public void handleStripeEvent(Event event) throws StripeException {
        switch (event.getType()) {
            case "customer.subscription.created": {
                Subscription subscription = handleSubscriptionEvent(event);

                if (subscription == null) {
                    throw new ResourceNotFoundException("Subscription not found");
                }

                stripeSubscriptionEventHandlerImpl.handleSubscriptionChange(subscription);
                break;
            }

            case "customer.subscription.updated": {
                Subscription subscription = handleSubscriptionEvent(event);
                stripeSubscriptionEventHandlerImpl.handleSubscriptionChange(subscription);
                break;
            }

            case "customer.subscription.deleted": {
                Subscription subscription = handleSubscriptionEvent(event);
                UserSubscription deletedUserSubscription = stripeSubscriptionEventHandlerImpl.handleSubscriptionCancellation(subscription);

                subscriptionEmailService.sendSubscriptionCancelledEmail(
                        deletedUserSubscription.getStripeCustomer().getEmail(),
                        deletedUserSubscription);
                break;
            }

            case "customer.subscription.paused": {

            }

            case "invoice.payment_succeeded": {
                Invoice invoice = handleInvoiceEvent(event);
                System.out.println(invoice);
                stripeInvoiceEventHandler.handleInvoiceSucceeded(invoice);
                break;
            }

            case "invoice.upcoming": {
                Invoice invoice = handleInvoiceEvent(event);
                stripeInvoiceEventHandler.handleUpcomingInvoice(invoice);
                break;
            }

            case "customer.deleted": {
                Customer customer = handleCustomerEvent(event);
                StripeCustomer stripeCustomer = stripeCustomerRepository.findStripeCustomerByStripeCustomerId(customer.getId())
                        .orElseThrow(() -> new CustomerNotFoundException("No customer found with id: " + customer.getId()));
                stripeCustomerRepository.delete(stripeCustomer);
                break;
            }

            default: {
                log.warn("Unhandled event type: {}", event.getType());
                break;
            }
        }
    }

    private Subscription handleSubscriptionEvent(Event event) {
        return (Subscription) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
    }

    private Customer handleCustomerEvent(Event event) {
        return (Customer) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
    }

    private Invoice handleInvoiceEvent(Event event) {
        return (Invoice) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
    }
}
