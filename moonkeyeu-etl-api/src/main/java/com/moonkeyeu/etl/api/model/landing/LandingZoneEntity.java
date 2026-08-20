package com.moonkeyeu.etl.api.model.landing;

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
@Table(name = "landing_zone", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LandingZoneEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "id", nullable = false)
    @JsonProperty("landing_zone_id")
    @EqualsAndHashCode.Include
    private Long landing_zone_id;
    @Basic
    @Column(name = "name", nullable = false)
    @JsonProperty("landing_location_name")
    private String name;
    @Basic
    @Column(name = "abbrev", nullable = true)
    @JsonProperty("landing_location_abbrev")
    private String abbrev;
    @Basic
    @Column(name = "description", nullable = true)
    @JsonProperty("landing_location_description")
    private String description;
    @Basic
    @Column(name = "successful_landings", nullable = true)
    @JsonProperty("zone_successful_landings")
    private Integer successful_landings;
    @Basic
    @Column(name = "location_id", nullable = true)
    private Integer location_id;

    @Override
    public Object getPrimaryKey() {
        return landing_zone_id;
    }
}
