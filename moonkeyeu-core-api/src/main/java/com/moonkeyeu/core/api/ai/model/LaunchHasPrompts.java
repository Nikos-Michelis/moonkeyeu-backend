package com.moonkeyeu.core.api.ai.model;

import com.moonkeyeu.core.api.launch.model.launch.Launch;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "launch_has_prompts")
public class LaunchHasPrompts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "launch_prompt_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ai_prompt_id", nullable = false)
    private AiPrompt aiPrompt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "launch_id", nullable = false)
    private Launch launch;

    /*@CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;*/

}