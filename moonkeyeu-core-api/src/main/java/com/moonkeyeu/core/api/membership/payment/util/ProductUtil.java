package com.moonkeyeu.core.api.membership.payment.util;

import com.moonkeyeu.core.api.membership.domain.model.SubscriptionPlan;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class ProductUtil {

    public Product getProductBySubscription(Subscription subscription) throws StripeException {
        SubscriptionItem subscriptionItem = subscription.getItems().getData().get(0);
        String productId = subscriptionItem.getPlan().getProduct();
        return Product.retrieve(productId);
    }

    public Product buildStripeProduct(SubscriptionPlan subscriptionPlan) {
        Product product = new Product();
        product.setId(subscriptionPlan.getStripeProductId());
        product.setName(subscriptionPlan.getName());
        //product.setDescription(subscriptionPlan.get);

        //Map<String, String> metadata = new HashMap<>();
        //metadata.put("app_tier", tier);
        //product.setMetadata(metadata);
        Price price = new Price();
        price.setId(subscriptionPlan.getStripePriceId());
        price.setUnitAmountDecimal(BigDecimal.valueOf(subscriptionPlan.getAmount()));
        product.setDefaultPriceObject(price);
        return product;
    }
}
