package com.moonkeyeu.core.api.subscription.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "plan")
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id", nullable = false)
    private Integer id;

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
    @Column(name = "token_limit", nullable = false)
    private Integer tokenLimit;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "tk_reset_hours_interval", nullable = false)
    private Integer tkResetHoursInterval;

    @OneToMany(mappedBy = "subscriptionPlan")
    @BatchSize(size = 20)
    private List<Subscription> subscriptions;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}