package com.moonkeyeu.core.api.membership.subscription.dto;

import java.time.Instant;

public record SubscriptionUsageDTO (Long subscription_usage_id, Instant last_usage) {}
