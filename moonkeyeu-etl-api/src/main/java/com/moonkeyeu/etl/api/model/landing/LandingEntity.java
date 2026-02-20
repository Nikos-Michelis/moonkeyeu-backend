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
@Table(name = "landing", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LandingEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "landing_id", nullable = false)
    @EqualsAndHashCode.Include
    private Long landing_id;
    @Basic
    @Column(name = "attempt", columnDefinition = "TINYINT(1)", nullable = true)
    private Boolean attempt;
    @Basic
    @Column(name = "success", nullable = true, length = 45)
    private String success;
    @Basic
    @Column(name = "description", nullable = true, length = -1)
    @JsonProperty("landing_description")
    private String description;
    @Basic
    @Column(name = "downrange_distance", nullable = true)
    private Long downrange_distance;
    @Basic
    @Column(name = "landing_zone_id")
    private Long landing_zone_id;
    @Basic
    @Column(name = "landing_type_id", nullable = true)
    @JsonProperty("type_id")
    private Long landing_type_id;

    @Override
    public Object getPrimaryKey() {
        return landing_id;
    }
}
