package com.moonkeyeu.core.api.subscription.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@DynamicUpdate
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subscription_usage", schema = "moonkey_db")
public class SubscriptionUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long subscription_usage_id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @Column(name = "expense_id", nullable = false)
    private String expenseId;

    @Column(name = "usage_count", nullable = false)
    private int usageCount;

    @Column(name = "usage_date", nullable = false)
    private Instant usageDate;
}