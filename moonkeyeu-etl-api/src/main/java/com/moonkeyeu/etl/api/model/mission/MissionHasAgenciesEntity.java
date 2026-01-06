package com.moonkeyeu.etl.api.model.mission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.PkBuilder;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "mission_has_agencies", schema = "moonkey_db", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"mission_id", "agency_id"})
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class MissionHasAgenciesEntity implements CsvEntity<Object>, PkBuilder {
    @Id
    @Column(name = "mission_agencies_id")
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

    @Override
    public void setPrimaryKey() {
        this.id = Long.valueOf(agency_id + "" + mission_id);
    }
}
