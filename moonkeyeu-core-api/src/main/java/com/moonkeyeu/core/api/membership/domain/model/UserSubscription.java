package com.moonkeyeu.core.api.membership.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_subscription", schema = "moonkey_db")
@EntityListeners(AuditingEntityListener.class)
public class UserSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;
    @Column(name = "stripe_subscription_id", nullable = false)
    private String stripeSubscriptionId;
    @Column(name = "expiration_reminder_sent")
    private boolean expirationReminderSent;
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "current_period_start")
    private Instant currentPeriodStart;
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "current_period_end")
    private Instant currentPeriodEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH } )
    @JoinColumn(name = "customer_id", nullable = false)
    private StripeCustomer stripeCustomer;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
