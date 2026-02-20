package com.moonkeyeu.core.api.launch.model.mission;

import com.moonkeyeu.core.api.launch.model.agency.Agencies;
import com.moonkeyeu.core.api.launch.model.launch.Launch;
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
public class Mission {
    @Id
    @Column(name = "mission_id")
    @EqualsAndHashCode.Include
    private Long missionId;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "type")
    private String type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orbit_id")
    private Orbit orbit;
    @OneToMany(mappedBy = "mission")
    @BatchSize(size = 10)
    private Set<Launch> launches;
    @ManyToMany
    @JoinTable(
            name = "mission_has_agencies",
            joinColumns = @JoinColumn(name = "mission_id"),
            inverseJoinColumns = @JoinColumn(name = "agency_id")
    )
    private Set<Agencies> agencies;
}
