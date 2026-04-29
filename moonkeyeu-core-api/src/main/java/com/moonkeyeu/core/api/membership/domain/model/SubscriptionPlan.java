package com.moonkeyeu.core.api.membership.domain.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "plan")
@EntityListeners(AuditingEntityListener.class)
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id", nullable = false)
    private Long id;
    @Size(max = 255)
    @NotNull
    @Column(name = "stripe_product_id", nullable = false)
    private String stripeProductId;
    @Size(max = 255)
    @NotNull
    @Column(name = "stripe_price_id", nullable = false)
    private String stripePriceId;
    @Size(max = 25)
    @NotNull
    @Column(name = "name", nullable = false, length = 25)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false)
    private ProductType productType;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "amount", nullable = false)
    private Double amount;
    @Column(name = "discount")
    private Integer discount;
    @NotNull
    @Column(name = "billing_cycle", nullable = false)
    private String billingCycle;
    @NotNull
    @ColumnDefault("0")
    @Column(name = "token_limit", nullable = false)
    private Integer tokenLimit;
    @NotNull
    @ColumnDefault("0")
    @Column(name = "tk_reset_hours_interval", nullable = false)
    private Integer tkResetHoursInterval;

    @OneToMany(mappedBy = "subscriptionPlan")
    @BatchSize(size = 20)
    private List<UserSubscription> userSubscriptions;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}