package com.moonkeyeu.core.api.subscription.aop;

import com.moonkeyeu.core.api.settings.exceptions.SubscriptionRequiredException;
import com.moonkeyeu.core.api.settings.exceptions.SubscriptionTokenLimitReachedException;
import com.moonkeyeu.core.api.subscription.model.Subscription;
import com.moonkeyeu.core.api.subscription.model.SubscriptionStatus;
import com.moonkeyeu.core.api.subscription.service.SubscriptionUsageService;
import com.moonkeyeu.core.api.user.model.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Aspect
@Slf4j
@Component
public class SubscriptionAspect {

    private final SubscriptionUsageService subscriptionUsageService;

    public SubscriptionAspect(SubscriptionUsageService subscriptionUsageService) {
        this.subscriptionUsageService = subscriptionUsageService;
    }


    @Transactional
    @Around("@annotation(com.moonkeyeu.core.api.subscription.aop.Subscribed)")
    public Object enforceSubscription(ProceedingJoinPoint joinPoint) throws Throwable {
        User user = getCurrentUser();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Subscribed annotation = method.getAnnotation(Subscribed.class);

        Subscription subscription = validateActiveSubscription(user, annotation);

        int token = resolveTokenAmount(joinPoint, signature, annotation);

        if (annotation.useToken()) {
            checkIntervalTokenLimit(subscription, token);
        }

        Object result = joinPoint.proceed();

        if (isSuccessfulResponse() && annotation.useToken()) {
            subscriptionUsageService.useFromLimit(subscription, annotation.expenseId(), token);
        }

        return result;
    }

    //Spring Security Authenticated User
    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    //Spring Servlet Context Response Status Control (Thread Local)
    private boolean isSuccessfulResponse() {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        return response != null && Set.of(HttpStatus.OK, HttpStatus.CREATED)
                .stream()
                .map(HttpStatusCode::value)
                .anyMatch(code -> code == response.getStatus());
    }

    private Subscription validateActiveSubscription(User user, Subscribed annotation) {
        System.out.println(Arrays.asList(annotation.products()));
        return Optional.ofNullable(user.getSubscription())
                .filter(sub -> sub.getStatus() != SubscriptionStatus.CANCELED)
                .filter(sub -> Arrays.asList(annotation.products()).contains(sub.getProduct()))
                .filter(sub -> sub.getExpirationAt().isAfter(LocalDateTime.now()))
                .orElseThrow(SubscriptionRequiredException::new);
    }

    private void checkIntervalTokenLimit(Subscription subscription, long tokenUsage) {
        long usage = subscriptionUsageService.getUsageByInterval(subscription, Instant.now());
        if ((usage + tokenUsage) > subscription.getTokenLimit()) {
            throw new SubscriptionTokenLimitReachedException();
        }
    }

    private int resolveTokenAmount(ProceedingJoinPoint joinPoint, MethodSignature signature, Subscribed annotation) {
        int token = 0;
        Map<String, Object> paramMap = buildMethodParamMap(signature, joinPoint.getArgs());
        EvaluationContext context = buildEvaluationContext(paramMap);
        ExpressionParser parser = new SpelExpressionParser();
        for (SubscriptionRule rule : annotation.rules()) {
            Boolean match = parser.parseExpression(rule.expression()).getValue(context, Boolean.class);
            if (Boolean.TRUE.equals(match)) {
                token += rule.token();
            }
        }
        return token;
    }

    private Map<String, Object> buildMethodParamMap(MethodSignature signature, Object[] args) {
        Map<String, Object> paramMap = new HashMap<>();
        String[] paramNames = signature.getParameterNames();

        for (int i = 0; i < paramNames.length; i++) {
            paramMap.put(paramNames[i], args[i]);
        }
        return paramMap;
    }

    private EvaluationContext buildEvaluationContext(Map<String, Object> paramMap) {
        EvaluationContext context = new StandardEvaluationContext();
        paramMap.forEach(context::setVariable);
        return context;
    }

}