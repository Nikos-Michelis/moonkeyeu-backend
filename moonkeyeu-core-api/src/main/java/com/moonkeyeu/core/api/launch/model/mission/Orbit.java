package com.moonkeyeu.core.api.launch.model.mission;

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
@Table(name = "orbit", schema = "moonkey_db")
public class Orbit {
    @Id
    @Column(name = "orbit_id")
    @EqualsAndHashCode.Include
    private Long orbitId;
    @Basic
    @Column(name = "name")
    private String orbitName;
    @Basic
    @Column(name = "abbrev")
    private String abbrev;
    @BatchSize(size = 10)
    @OneToMany(mappedBy = "orbit")
    private Set<Mission> missions;
}
