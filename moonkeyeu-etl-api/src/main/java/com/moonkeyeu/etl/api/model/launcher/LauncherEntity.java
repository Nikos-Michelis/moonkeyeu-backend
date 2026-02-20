package com.moonkeyeu.etl.api.model.launcher;

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
@Table(name = "launcher", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LauncherEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "launcher_id")
    @EqualsAndHashCode.Include
    private Long launcher_id;
    @Basic
    @Column(name = "details")
    private String details;
    @Basic
    @Column(name = "flight_proven", columnDefinition = "TINYINT(1)")
    private Boolean flight_proven;
    @Basic
    @Column(name = "serial_number")
    private String serial_number;
    @Basic
    @Column(name = "successful_landings")
    @JsonProperty("launcher_successful_landings")
    private Integer successful_landings;
    @Basic
    @Column(name = "attempted_landings")
    private Integer attempted_landings;
    @Basic
    @Column(name = "flights")
    private Integer flights;
    @Basic
    @Column(name = "last_launch_date", columnDefinition = "DATETIME")
    private Instant last_launch_date;
    @Basic
    @Column(name = "first_launch_date", columnDefinition = "DATETIME")
    private Instant first_launch_date;
    @Basic
    @Column(name = "status_id")
    private Long status_id;

    @Override
    public Object getPrimaryKey() {
        return launcher_id;
    }
}
