package com.moonkeyeu.etl.api.model.spacecraft;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moonkeyeu.etl.api.model.CsvEntity;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "spacecraft_configuration", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpacecraftConfigurationEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long spacecraft_conf_id;
    @Basic
    @Column(name = "name")
    private String spacecraft_conf_name;
    @Column(name = "type_id")
    @JsonProperty("spacecraft_type_id")
    private Long type_id;
    @Basic
    @Column(name = "in_use", columnDefinition = "TINYINT(1)")
    private Boolean in_use;
    @Basic
    @Column(name = "capability")
    private String capability;
    @Basic
    @Column(name = "history")
    private String history;
    @Basic
    @Column(name = "details")
    private String details;
    @Basic
    @Column(name = "maiden_flight")
    private Date maiden_flight;
    @Basic
    @Column(name = "height")
    private Double height;
    @Basic
    @Column(name = "diameter")
    private Double diameter;
    @Basic
    @Column(name = "human_rated", columnDefinition = "TINYINT(1)")
    private Boolean human_rated;
    @Basic
    @Column(name = "crew_capacity")
    private Integer crew_capacity;
    @Basic
    @Column(name = "payload_capacity")
    private Integer payload_capacity;
    @Basic
    @Column(name = "payload_return_capacity")
    private Integer payload_return_capacity;
    @Basic
    @Column(name = "flight_life")
    private String flight_life;
    @Basic
    @Column(name = "wiki_link")
    private String wiki_link;
    @Basic
    @Column(name = "info_link")
    private String info_link;
    @Column(name = "agency_id")
    private Long agency_id;

    @Override
    public Object getPrimaryKey() {
        return spacecraft_conf_id;
    }
}
