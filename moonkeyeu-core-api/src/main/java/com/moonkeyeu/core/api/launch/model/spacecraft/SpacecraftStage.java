package com.moonkeyeu.core.api.launch.model.spacecraft;

import com.moonkeyeu.core.api.launch.model.astronaut.CrewMember;
import com.moonkeyeu.core.api.launch.model.landing.Landing;
import com.moonkeyeu.core.api.launch.model.rocket.Rocket;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "spacecraft_stage", schema = "moonkey_db")
public class SpacecraftStage {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long spacecraftStageId;
    @Basic
    @Column(name = "mission_end")
    private String missionEnd;
    @Basic
    @Column(name = "destination")
    private String destination;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rocket_id")
    private Rocket rocket;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spacecraft_id")
    private Spacecraft spacecraft;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landing_id")
    private Landing landing;
    @OneToMany(mappedBy = "spacecraftStage")
    @BatchSize(size = 10)
    private Set<CrewMember> crewMembers;
}
