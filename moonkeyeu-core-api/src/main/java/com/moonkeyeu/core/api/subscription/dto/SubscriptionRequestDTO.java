package com.moonkeyeu.core.api.subscription.dto;

import com.moonkeyeu.core.api.subscription.model.ProductType;
import com.moonkeyeu.core.api.subscription.model.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SubscriptionRequestDTO {
    private ProductType productType;
    private SubscriptionStatus status;
    private Instant purchasedAt;
    private Instant subscriptionExpireDate;
}
