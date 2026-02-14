package com.moonkeyeu.core.api.launch.model.astronaut;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Role {
    @Id
    @Column(name = "role_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name")
    private String roleName;

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    @BatchSize(size = 10)
    @JsonManagedReference
    private Set<CrewMember> crewMembers;
}
