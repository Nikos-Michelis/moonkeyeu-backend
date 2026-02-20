package com.moonkeyeu.etl.api.model.pad;

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
@Table(name = "pad_has_agencies", schema = "moonkey_db", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"launch_pad_id", "agency_id"})
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class LaunchPadHasAgenciesEntity implements CsvEntity<Object>, PkBuilder {
    @Id
    @Column(name = "pad_agency_id")
    @EqualsAndHashCode.Include
    private Long id;
    @Basic
    @Column(name = "launch_pad_id")
    private Long launch_pad_id;
    @Basic
    @Column(name = "agency_id")
    private Long agency_id;

    @Override
    public Object getPrimaryKey() {
        setPrimaryKey();
        return id;
    }

    @Override
    public void setPrimaryKey() {
        this.id = Long.valueOf(agency_id + "" + launch_pad_id);
    }
}
