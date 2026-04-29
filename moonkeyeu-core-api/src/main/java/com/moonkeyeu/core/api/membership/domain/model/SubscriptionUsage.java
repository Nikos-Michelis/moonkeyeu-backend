package com.moonkeyeu.core.api.membership.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@DynamicUpdate
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subscription_usage", schema = "moonkey_db")
@EntityListeners(AuditingEntityListener.class)
public class SubscriptionUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long subscription_usage_id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "subscription_id")
    private UserSubscription userSubscription;

    @Column(name = "expense_id", nullable = false)
    private String expenseId;

    @Column(name = "usage_count", nullable = false)
    private int usageCount;

    @Column(name = "usage_date", nullable = false)
    private Instant usageDate;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

}