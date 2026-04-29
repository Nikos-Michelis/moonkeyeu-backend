package com.moonkeyeu.core.api.membership.subscription.dto;

import com.moonkeyeu.core.api.membership.domain.model.SubscriptionStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserSubscriptionDTO {
    @NotBlank(message = "Stripe subscription ID is mandatory")
    private String stripeSubscriptionId;

    @NotNull(message = "Status is required")
    private SubscriptionStatus status;

    private boolean expirationReminderSent;

    @NotNull(message = "Start date is required")
    private Instant currentPeriodStart;

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must be in the present or future")
    private Instant currentPeriodEnd;

    @NotNull(message = "Plan ID is required")
    private Long planId;

    @NotBlank(message = "Customer ID is required")
    private String stripeCustomerId;
}
