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
@Table(name = "orbit", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrbitEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long orbit_id;
    @Basic
    @Column(name = "name")
    @JsonProperty("orbit_name")
    private String name;
    @Basic
    @Column(name = "abbrev")
    @JsonProperty("orbit_abbrev")
    private String abbrev;

    @Override
    public Object getPrimaryKey() {
        return orbit_id;
    }
}
