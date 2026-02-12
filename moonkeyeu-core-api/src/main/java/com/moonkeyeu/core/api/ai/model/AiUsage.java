package com.moonkeyeu.core.api.ai.model;

import com.moonkeyeu.core.api.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

import static jakarta.persistence.CascadeType.*;

@Getter
@Setter
@Entity
@Table(name = "ai_usage")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AiUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_usage_id", nullable = false)
    private Long id;

    @NotNull
    @OneToOne(mappedBy = "usage", cascade = { PERSIST, MERGE, REFRESH })
    private AiPrompt prompt;

    @NotNull
    /*@ManyToOne(fetch = FetchType.LAZY, optional = false)*/
    @Column(name = "ai_provider_id", nullable = false)
    private Integer providerId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "prompt_tokens", nullable = false)
    private Integer promptTokens;

    @NotNull
    @Column(name = "completion_tokens", nullable = false)
    private Integer completionTokens;

    @NotNull
    @Column(name = "total_tokens", nullable = false)
    private Integer totalTokens;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}