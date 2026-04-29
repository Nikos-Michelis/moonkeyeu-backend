package com.moonkeyeu.core.api.assistant.model;

import com.moonkeyeu.core.api.launch.model.astronaut.Astronaut;
import com.moonkeyeu.core.api.launch.model.launch.Launch;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_prompt", schema = "moonkey_db")
@EntityListeners(AuditingEntityListener.class)
public class AiPrompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_prompt_id")
    private Long promptId;

    @OneToOne(cascade = { PERSIST, MERGE, REFRESH })
    @JoinColumn(name = "ai_usage_id", unique = true)
    private AiUsage usage;

    @Lob
    @Column(name = "prompt")
    private String prompt;

    @Lob
    @Column(name = "completion")
    private String completion;

    @ManyToMany
    @JoinTable(
            name = "launch_has_prompts",
            joinColumns = @JoinColumn(name = "ai_prompt_id"),
            inverseJoinColumns = @JoinColumn(name = "launch_id")
    )
    @BatchSize(size = 20)
    private Set<Launch> launches;

    @ManyToMany
    @JoinTable(
            name = "astronaut_has_prompts",
            joinColumns = @JoinColumn(name = "ai_prompt_id"),
            inverseJoinColumns = @JoinColumn(name = "astronaut_id")
    )
    @BatchSize(size = 20)
    private Set<Astronaut> astronauts;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public void addAstronaut(Astronaut astronaut) {
        this.getAstronauts().add(astronaut);
        astronaut.getAiPrompts().add(this);
    }

    public void addLaunch(Launch launch) {
        this.getLaunches().add(launch);
        launch.getAiPrompts().add(this);
    }

    public Set<Launch> getLaunches() {
        if (launches == null) {
            setLaunches(new HashSet<>());
        }
        return launches;
    }

    public Set<Astronaut> getAstronauts() {
        if (astronauts == null) {
            setAstronauts(new HashSet<>());
        }
        return astronauts;
    }

    public void deletePrompt(AiPrompt aiPrompt) {
        aiPrompt.getLaunches().removeIf(launch -> launch.getAiPrompts().remove(aiPrompt));
        this.launches.clear();
    }
}