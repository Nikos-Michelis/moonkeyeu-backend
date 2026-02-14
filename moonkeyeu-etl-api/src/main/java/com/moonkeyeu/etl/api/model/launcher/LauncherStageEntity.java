package com.moonkeyeu.etl.api.model.launcher;

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
@Table(name = "launcher_stage", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LauncherStageEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "launcher_stage_id")
    @EqualsAndHashCode.Include
    private Long launcher_stage_id;
    @Basic
    @Column(name = "type")
    @JsonProperty("booster_type")
    private String type;
    @Basic
    @Column(name = "reused", columnDefinition = "TINYINT(1)")
    private Boolean reused;
    @Basic
    @Column(name = "launcher_flight_number")
    private String launcher_flight_number;
    @Basic
    @Column(name = "rocket_id")
    private Long rocket_id;
    @Basic
    @Column(name = "launcher_id")
    private Long launcher_id;
    @Basic
    @Column(name = "landing_id")
    private Long landing_id;

    @Override
    public Object getPrimaryKey() {
        return launcher_stage_id;
    }
}
