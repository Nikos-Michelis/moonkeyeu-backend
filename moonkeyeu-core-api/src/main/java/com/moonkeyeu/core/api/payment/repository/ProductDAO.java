package com.moonkeyeu.core.api.payment.repository;

import com.stripe.model.Price;
import com.stripe.model.Product;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ProductDAO {

    static Product[] products = new Product[3];

    static {

        // 2. Basic Tier (Basic)
        products[0] = createProduct("prod_Tzm0z1oN8ouFSi", "MoonkeyEU - Basic", "price_1T1mShGrKq02A5TNFXOnBdej",250, "BASIC",
                "Standard comfort. Includes access to the Basic rewards shop.");

        // 3.  Pro Tier (Pro)
        products[1] = createProduct("prod_Tzm2u3xWKo48RB", "MoonkeyEU - Pro", "price_1T1mTwGrKq02A5TNanGKroT3",450, "PRO",
                "Lossless audio and offline downloads. EXCLUSIVE to Pro members.");
    }

    private static Product createProduct(String id, String name, String priceId, long amount, String tier, String description) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setDescription(description);

        // Tag product with the minimum tier level needed to see it
        Map<String, String> metadata = new HashMap<>();
        metadata.put("app_tier", tier);
        p.setMetadata(metadata);

        Price price = new Price();
        price.setId(priceId);
        price.setCurrency("EUR");
        price.setUnitAmountDecimal(BigDecimal.valueOf(amount));
        p.setDefaultPriceObject(price);
        return p;
    }

    public static Product getProduct(String id) {
        for (Product p : products) {
            if (p.getId().equals(id)) return p;
        }
        return new Product();
    }
}