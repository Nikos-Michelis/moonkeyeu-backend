package com.moonkeyeu.etl.api.model.mission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moonkeyeu.etl.api.model.CsvEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "mission", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MissionEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    private Long mission_id;
    @Basic
    @Column(name = "name")
    @JsonProperty("mission_name")
    private String name;
    @Basic
    @Column(name = "description")
    @JsonProperty("mission_description")
    private String description;
    @Basic
    @Column(name = "type")
    @JsonProperty("mission_type")
    private String type;
    @Basic
    @Column(name = "orbit_id")
    private Long orbit_id;

    @Override
    public Object getPrimaryKey() {
        return mission_id;
    }
}
