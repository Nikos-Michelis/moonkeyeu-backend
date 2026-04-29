package com.moonkeyeu.core.api.membership.domain.model;

import lombok.Getter;

@Getter
public enum BillingReason {
    MANUAL("MANUAL", "Manually created invoice"),
    SUBSCRIPTION_CREATE("SUBSCRIPTION CREATE", "Invoice created when subscription starts"),
    SUBSCRIPTION_CYCLE("SUBSCRIPTION CYCLE", "Regular subscription billing cycle"),
    SUBSCRIPTION_UPDATE("SUBSCRIPTION UPDATE", "Invoice due to subscription changes"),
    SUBSCRIPTION_THRESHOLD("SUBSCRIPTION THRESHOLD", "Invoice created due to billing threshold"),
    UPCOMING("UPCOMING", "Upcoming invoice preview");
    
    private final String name;
    private final String description;
    
    BillingReason(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public boolean isSubscriptionRelated() {
        return this == SUBSCRIPTION_CREATE 
            || this == SUBSCRIPTION_CYCLE 
            || this == SUBSCRIPTION_UPDATE 
            || this == SUBSCRIPTION_THRESHOLD;
    }
    
    public boolean isAutomatic() {
        return this != MANUAL;
    }
}