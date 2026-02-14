package com.moonkeyeu.core.api.launch.model.launcher;

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
@Table(name = "launcher_status", schema = "moonkey_db")
public class LauncherStatus {
    @Id
    @Column(name = "status_id")
    @EqualsAndHashCode.Include
    private Long statusId;
    @Basic
    @Column(name = "name")
    private String statusName;
    @OneToMany(mappedBy = "status")
    @BatchSize(size = 10)
    private Set<Launcher> launchers;
}
