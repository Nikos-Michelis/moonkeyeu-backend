package com.moonkeyeu.core.api.launch.model.launch;

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
@Table(name = "launch_status", schema = "moonkey_db")
public class LaunchStatus {
    @Id
    @Column(name = "status_id")
    @EqualsAndHashCode.Include
    private Long statusId;
    @Basic
    @Column(name = "name")
    private String statusName;
    @Basic
    @Column(name = "abbrev")
    private String abbrev;
    @Basic
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy = "launchStatus")
    @BatchSize(size = 10)
    private Set<Launch> launches;
}
