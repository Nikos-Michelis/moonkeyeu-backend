package com.moonkeyeu.core.api.membership.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "invoice", schema = "moonkey_db")
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripeInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "invoice_stripe_id", nullable = false)
    private String invoiceStripeId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "subscription_id", nullable = false)
    private UserSubscription subscription;

    @NotNull
    @ColumnDefault("0.00")
    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", length = 3)
    private Currency currency;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvoiceStatus status = InvoiceStatus.UNCOLLECTIBLE;
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_reason", length = 30)
    private BillingReason billingReason;

    @Size(max = 255)
    @NotNull
    @Column(name = "hosted_invoice_url", nullable = false)
    private String hostedInvoiceUrl;
    @Column(name = "next_payment_attempt")
    private Instant nextPaymentAttempt;
    @Column(name = "invoice_created_at")
    private Instant invoiceCreatedAt;
    @Column(name = "finalized_at")
    private Instant finalizedAt;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

}