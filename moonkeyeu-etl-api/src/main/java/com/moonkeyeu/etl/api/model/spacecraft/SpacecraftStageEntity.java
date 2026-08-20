package com.moonkeyeu.etl.api.model.spacecraft;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.moonkeyeu.etl.api.model.CsvEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "spacecraft_stage", schema = "moonkey_db")
@JsonPropertyOrder({"spacecraft_stage_id", "mission_end", "destination", "rocket_id", "spacecraft_id", "landing_id"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpacecraftStageEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long spacecraft_stage_id;
    @Basic
    @Column(name = "mission_end")
    private String mission_end;
    @Basic
    @Column(name = "destination")
    private String destination;
    @Column(name = "rocket_id")
    private Long rocket_id;
    @Column(name = "spacecraft_id")
    private Long spacecraft_id;
    @Column(name = "landing_id")
    private Long landing_id;

    @Override
    public Object getPrimaryKey() {
        return spacecraft_stage_id;
    }
}
