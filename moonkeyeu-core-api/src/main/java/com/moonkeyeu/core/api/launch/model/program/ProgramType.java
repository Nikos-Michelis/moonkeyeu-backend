package com.moonkeyeu.core.api.launch.model.program;

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
@Table(name = "program_type", schema = "moonkey_db")
public class ProgramType {
    @Id
    @Column(name = "type_id")
    @EqualsAndHashCode.Include
    private Long typeId;
    @Basic
    @Column(name = "name")
    private String typeName;
    @OneToMany(mappedBy = "type")
    private Set<Programs> programs;
}
