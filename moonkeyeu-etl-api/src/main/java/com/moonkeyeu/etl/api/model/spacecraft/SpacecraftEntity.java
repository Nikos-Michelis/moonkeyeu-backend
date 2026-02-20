package com.moonkeyeu.etl.api.model.spacecraft;

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
@Table(name = "spacecraft", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpacecraftEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "spacecraft_id")
    @EqualsAndHashCode.Include
    private Long spacecraft_id;
    @Basic
    @Column(name = "name")
    @JsonProperty("spacecraft_name")
    private String name;
    @Basic
    @Column(name = "serial_number")
    private String serial_number;
    @Basic
    @Column(name = "is_placeholder", columnDefinition = "TINYINT(1)")
    private Boolean is_placeholder;
    @Basic
    @Column(name = "in_space", columnDefinition = "TINYINT(1)")
    private Boolean in_space;
    @Basic
    @Column(name = "flights_count")
    private Integer flights_count;
    @Basic
    @Column(name = "mission_ends_count")
    private Integer mission_ends_count;
    @Basic
    @Column(name = "description")
    @JsonProperty("spacecraft_description")
    private String description;
    @Basic
    @Column(name = "spacecraft_conf_id")
    private Long spacecraft_conf_id;
    @Basic
    @Column(name = "status_id")
    private Long status_id;

    @Override
    public Object getPrimaryKey() {
        return spacecraft_id;
    }
}
