package com.moonkeyeu.core.api.launch.model.astronaut;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "astronaut_status", schema = "moonkey_db")
public class AstronautStatus {
    @Id
    @Column(name = "status_id")
    @EqualsAndHashCode.Include
    private Long statusId;
    @Basic
    @Column(name = "name")
    private String statusName;
    @OneToMany(mappedBy = "status", fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Astronaut> astronauts;
}
