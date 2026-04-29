package com.moonkeyeu.core.api.membership.subscription.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanDTO {
    private String name;
    private String productType;
    private Double amount;
    private String billingCycle;
    private Integer tokenLimit;
    private Integer tkResetHoursInterval;
}
