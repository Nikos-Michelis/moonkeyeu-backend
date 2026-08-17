package com.moonkeyeu.etl.api.model.launch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moonkeyeu.etl.api.model.CsvEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "launch", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LaunchEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private String launch_id;
    @Basic
    @Column(name = "slug")
    private String slug;
    @Basic
    @Column(name = "flightclub_url")
    private String flightclub_url;
    @Basic
    @Column(name = "name")
    @JsonProperty("launch_name")
    private String name;
    @Basic
    @Column(name = "last_updated", columnDefinition = "DATETIME")
    private Instant last_updated;
    @Basic
    @Column(name = "net", columnDefinition = "DATETIME")
    private Instant net;
    @Basic
    @Column(name = "window_start", columnDefinition = "DATETIME")
    private Instant window_start;
    @Basic
    @Column(name = "window_end", columnDefinition = "DATETIME")
    private Instant window_end;
    @Basic
    @Column(name = "probability")
    private Double probability;
    @Basic
    @Column(name = "weather_concerns")
    private String weather_concerns;
    @Basic
    @Column(name = "agency_id")
    private Long agency_id;
    @Basic
    @Column(name = "rocket_id")
    private Long rocket_id;
    @Basic
    @Column(name = "mission_id")
    private Long mission_id;
    @Basic
    @Column(name = "launch_pad_id")
    private Long launch_pad_id;
    @Basic
    @Column(name = "status_id")
    private String status_id;
    @Column(name = "net_precision_id")
    private Long net_precision_id;
    @Override
    public Object getPrimaryKey() {
        return launch_id;
    }
}
