package com.moonkeyeu.core.api.membership.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idempotency_id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    @Size(max = 255)
    @NotNull
    @Column(name = "request_path", nullable = false)
    private String requestPath;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "locked_at", nullable = false)
    private Instant lockedAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToOne(mappedBy = "idempotency")
    private Payment payment;

}