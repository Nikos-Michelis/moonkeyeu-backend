package com.moonkeyeu.etl.api.model.programs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.PkBuilder;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "launch_has_programs", schema = "moonkey_db", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"program_id", "launch_id"})
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class LaunchHasProgramsEntity implements CsvEntity<Object>, PkBuilder {
    @Id
    @Column(name = "launch_programs_id")
    @EqualsAndHashCode.Include
    private String id;
    @Basic
    @Column(name = "program_id")
    private Long program_id;
    @Basic
    @Column(name = "launch_id")
    private String launch_id;

    @Override
    public Object getPrimaryKey() {
        setPrimaryKey();
        return id;
    }

    @Override
    public void setPrimaryKey() {
        this.id = launch_id + program_id;
    }
}
