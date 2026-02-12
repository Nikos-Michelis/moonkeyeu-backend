package com.moonkeyeu.core.api.subscription.aop;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SubscriptionRule {
    String expression() default "1 == 1";
    int token();
}