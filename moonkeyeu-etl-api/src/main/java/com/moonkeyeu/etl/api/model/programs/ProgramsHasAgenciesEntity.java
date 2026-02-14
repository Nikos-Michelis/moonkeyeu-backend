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
@Table(name = "programs_has_agencies", schema = "moonkey_db", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"program_id", "agency_id"})
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgramsHasAgenciesEntity implements CsvEntity<Object>, PkBuilder {
    @Id
    @Column(name = "program_agency_id")
    @EqualsAndHashCode.Include
    private long id;
    @Basic
    @Column(name = "program_id")
    private long program_id;
    @Basic
    @Column(name = "agency_id")
    private long agency_id;

    @Override
    public Object getPrimaryKey() {
        setPrimaryKey();
        return id;
    }

    @Override
    public void setPrimaryKey() {
        this.id = Long.parseLong(program_id + "" + agency_id);
    }
}
