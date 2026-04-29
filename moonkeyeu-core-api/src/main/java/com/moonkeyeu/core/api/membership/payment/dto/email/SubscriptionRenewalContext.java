package com.moonkeyeu.core.api.membership.payment.dto.email;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class SubscriptionRenewalContext {
    private String appName;
    private String email;
    private String planName;
    private String renewalDate;
    private String nextBillingDate;
    private String billingCycle;
    private String amount;
    private String invoiceUrl;
    private String paymentMethod;
    private String dashboardUrl;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("appName", appName);
        map.put("email", email);
        map.put("planName", planName);
        map.put("renewalDate", renewalDate);
        map.put("nextBillingDate", nextBillingDate);
        map.put("billingCycle", billingCycle);
        map.put("amount", amount);
        map.put("paymentMethod", paymentMethod);
        map.put("dashboardUrl", dashboardUrl);

        if (invoiceUrl != null && !invoiceUrl.isEmpty()) {
            map.put("invoiceUrl", invoiceUrl);
        }

        return map;
    }
}