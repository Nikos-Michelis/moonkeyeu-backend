package com.moonkeyeu.core.api.membership.payment.dto;

import com.moonkeyeu.core.api.membership.domain.model.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class StripeSubscriptionDTO {
    private String stripeSubscriptionId;
    private SubscriptionStatus status;
    private boolean expirationReminderSent;
    private Instant currentPeriodStart;
    private Instant currentPeriodEnd;
}
