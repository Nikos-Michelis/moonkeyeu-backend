package com.moonkeyeu.core.api.launch.model.spacecraft;

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
@Table(name = "spacecraft_type", schema = "moonkey_db")
public class SpacecraftType {
    @Id
    @Column(name = "type_id")
    @EqualsAndHashCode.Include
    private Long typeId;
    @Basic
    @Column(name = "name")
    private String typeName;
    @OneToMany(mappedBy = "spacecraftType")
    @BatchSize(size = 10)
    private Set<SpacecraftConfiguration> spacecraftConfigurations;
}
