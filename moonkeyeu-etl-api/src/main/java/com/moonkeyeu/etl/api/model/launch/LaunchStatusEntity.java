package com.moonkeyeu.etl.api.model.launch;

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
@Table(name = "launch_status", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LaunchStatusEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "status_id", nullable = false)
    @EqualsAndHashCode.Include
    private Long status_id;
    @Basic
    @Column(name = "name")
    @JsonProperty("status_name")
    private String name;
    @Basic
    @Column(name = "abbrev")
    @JsonProperty("status_abbrev")
    private String abbrev;
    @Basic
    @Column(name = "description")
    @JsonProperty("status_description")
    private String description;

    @Override
    public Object getPrimaryKey() {
        return status_id;
    }
}
