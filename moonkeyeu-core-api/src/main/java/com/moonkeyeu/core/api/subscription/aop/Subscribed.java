package com.moonkeyeu.core.api.subscription.aop;

import com.moonkeyeu.core.api.subscription.model.ProductType;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribed {
    ProductType[] products() default {ProductType.TRAIL, ProductType.PRO};

    String expenseId();

    SubscriptionRule[] rules() default {};

    boolean useToken() default true;
}