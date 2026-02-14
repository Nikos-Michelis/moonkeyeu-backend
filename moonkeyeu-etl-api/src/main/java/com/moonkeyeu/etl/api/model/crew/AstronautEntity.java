package com.moonkeyeu.etl.api.model.crew;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moonkeyeu.etl.api.model.CsvEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "astronaut", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AstronautEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "astronaut_id", nullable = false)
    @EqualsAndHashCode.Include
    private Long astronaut_id;
    @Basic
    @Column(name = "name", nullable = false, length = 45)
    @JsonProperty("astronaut_name")
    private String name;
    @Basic
    @Column(name = "in_space", columnDefinition = "TINYINT(1)", nullable = true)
    private Boolean in_space;
    @Basic
    @Column(name = "date_of_death", nullable = true, length = 45)
    private LocalDate date_of_death;
    @Basic
    @Column(name = "date_of_birth", nullable = true, length = 45)
    private LocalDate date_of_birth;
    @Basic
    @Column(name = "age", nullable = true)
    private Integer age;
    @Basic
    @Column(name = "bio", nullable = true, length = -1)
    private String bio;
    @Basic
    @Column(name = "wiki_url", nullable = true, length = 255)
    @JsonProperty("wiki")
    private String wiki_url;
    @Basic
    @Column(name = "last_flight", columnDefinition = "DATETIME", nullable = true)
    private Instant last_flight;
    @Basic
    @Column(name = "first_flight", columnDefinition = "DATETIME", nullable = true)
    private Instant first_flight;
    @Basic
    @Column(name = "status_id")
    private Integer status_id;
    @Basic
    @Column(name = "agency_id")
    private Integer agency_id;

    @Override
    public Object getPrimaryKey() {
        return astronaut_id;
    }
}
