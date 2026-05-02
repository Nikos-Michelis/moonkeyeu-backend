package com.moonkeyeu.etl.api.model.mission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moonkeyeu.etl.api.model.CsvEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "mission_has_agencies", schema = "moonkey_db", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"mission_id", "agency_id"})
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class MissionHasAgenciesEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "mission_agencies_id")
    @EqualsAndHashCode.Include
    private Long id;
    @Basic
    @Column(name = "mission_id")
    private Long mission_id;
    @Basic
    @Column(name = "agency_id")
    private Long agency_id;

    @Override
    public Object getPrimaryKey() {
        setPrimaryKey();
        return id;
    }

    public void setPrimaryKey() {
        this.id = Long.valueOf(agency_id + "" + mission_id);
    }
}
