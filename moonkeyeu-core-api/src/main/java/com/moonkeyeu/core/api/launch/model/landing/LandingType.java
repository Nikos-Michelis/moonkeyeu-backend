package com.moonkeyeu.core.api.launch.model.landing;

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
@Table(name = "landing_type", schema = "moonkey_db")
public class LandingType {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long landingTypeId;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "abbrev")
    private String abbrev;
    @Basic
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy = "landingType")
    @BatchSize(size = 10)
    private Set<Landing> landings;
}
