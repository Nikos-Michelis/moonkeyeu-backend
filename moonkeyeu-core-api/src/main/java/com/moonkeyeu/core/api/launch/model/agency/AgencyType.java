package com.moonkeyeu.core.api.launch.model.agency;

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
@Table(name = "agency_type", schema = "moonkey_db")
public class AgencyType {
    @Id
    @Column(name = "type_id")
    @EqualsAndHashCode.Include
    private Long typeId;
    @Basic
    @Column(name = "name")
    private String typeName;
    @OneToMany(mappedBy = "agencyType")
    @BatchSize(size = 20)
    private Set<Agencies> agencies;
}
