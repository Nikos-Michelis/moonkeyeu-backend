package com.moonkeyeu.core.api.payment.util;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.SubscriptionItemListParams;
import com.stripe.param.SubscriptionListParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.Recurring;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.Recurring.Interval;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubscriptionItemUtil {

    public SessionCreateParams.LineItem buildSubscriptionLineItem(Product product, Interval interval) {
        var recurring = getRecurring(interval);
        var productData = getProductData(product);
        var priceData = getPriceData(product, recurring, productData);
        return SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(priceData)
                .build();
    }

    /**
     * Constructs the parameters required to retrieve a list of subscriptions for a customer.
     * @param customerId The Stripe customerId string whose subscriptions are being queried.
     * @return A {@link SubscriptionListParams} object configured for active subscriptions.
     */
    public SubscriptionListParams buildSubscriptionListParams(String customerId) {
        return SubscriptionListParams.builder()
                .setCustomer(customerId)
                .setStatus(SubscriptionListParams.Status.ACTIVE)
                .setStatus(SubscriptionListParams.Status.TRIALING)
                .build();
    }

    /**
     * Constructs the parameters required to retrieve individual line items for a specific subscription.
     * <p>
     * This method utilizes <b>Object Expansion</b> to include the full Product object
     * ({@code data.price.product}) within the response. This allows access to product metadata
     * (like app_id) without making additional API calls.
     * </p>
     *
     * @param subscription The specific {@link Subscription} to inspect.
     * @return A {@link SubscriptionItemListParams} object with expanded product details.
     */
    public SubscriptionItemListParams buildSubscriptionItemListParams(Subscription subscription) {
        return SubscriptionItemListParams.builder()
                .setSubscription(subscription.getId())
                .addExpand("data.price.product")
                .build();
    }

    public SubscriptionCollection getSubscriptionCollection(SubscriptionListParams subscriptionListParams) throws StripeException {
        return Subscription.list(subscriptionListParams);
    }

    public SubscriptionItemCollection getSubscriptionItemCollection(SubscriptionItemListParams subscriptionItemListParams) throws StripeException {
        return SubscriptionItem.list(subscriptionItemListParams);
    }

    private Recurring getRecurring(Interval interval) {
        return Recurring.builder()
                .setInterval(interval)
                .build();
    }

    private ProductData getProductData(Product product) {
        System.out.println(product.getMetadata().get("app_tier"));
        return ProductData.builder()
                .putMetadata("app_id", product.getId())
                .putMetadata("app_tier", product.getMetadata().get("app_tier"))
                .setName(product.getName())
                .build();
    }

    private PriceData getPriceData(Product product, Recurring recurring, ProductData productData) {
        return PriceData.builder()
                .setCurrency(product.getDefaultPriceObject().getCurrency())
                .setUnitAmountDecimal(product.getDefaultPriceObject().getUnitAmountDecimal())
                .setRecurring(recurring)
                .setProductData(productData)
                .build();
    }
}
