package com.moonkeyeu.etl.api.model.crew;

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
@Table(name = "astronaut_status", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AstronautStatusEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    private Long status_id;
    @Basic
    @Column(name = "name", nullable = false, length = 255)
    @JsonProperty("status_name")
    private String name;

    @Override
    public Object getPrimaryKey() {
        return status_id;
    }
}
