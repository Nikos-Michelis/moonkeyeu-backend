package com.moonkeyeu.etl.api.model.rocket;

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
@Table(name = "rocket_configuration", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RocketConfigurationEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long rocket_conf_id;
    @Basic
    @Column(name = "name")
    @JsonProperty("rocket_name")
    private String name;
    @Basic
    @Column(name = "variant")
    @JsonProperty("rocket_variant")
    private String variant;
    @Basic
    @Column(name = "fullname")
    private String fullName;
    @Basic
    @Column(name = "active", columnDefinition = "TINYINT(1)")
    private Boolean active;
    @Basic
    @Column(name = "reusable", columnDefinition = "TINYINT(1)")
    private Boolean reusable;
    @Basic
    @Column(name = "description")
    @JsonProperty("rocket_description")
    private String description;
    @Basic
    @Column(name = "alias")
    private String alias;
    @Basic
    @Column(name = "min_stage")
    private Integer min_stage;
    @Basic
    @Column(name = "max_stage")
    private Integer max_stage;
    @Basic
    @Column(name = "maiden_flight")
    private Date maiden_flight;
    @Basic
    @Column(name = "length")
    private Double length;
    @Basic
    @Column(name = "diameter")
    private Double diameter;
    @Basic
    @Column(name = "launch_cost")
    private Double launch_cost;
    @Basic
    @Column(name = "launch_mass")
    private Double launch_mass;
    @Basic
    @Column(name = "leo_capacity")
    private Double leo_capacity;
    @Basic
    @Column(name = "gto_capacity")
    private Double gto_capacity;
    @Basic
    @Column(name = "geo_capacity")
    private Double geo_capacity;
    @Basic
    @Column(name = "sso_capacity")
    private Double sso_capacity;
    @Basic
    @Column(name = "to_thrust")
    private Integer to_thrust;
    @Basic
    @Column(name = "apogee")
    private Integer apogee;
    @Basic
    @Column(name = "info_url")
    @JsonProperty("rocket_info_url")
    private String info_url;
    @Basic
    @Column(name = "wiki_url")
    @JsonProperty("rocket_wiki_url")
    private String wiki_url;
    @Basic
    @Column(name = "total_launch_count")
    @JsonProperty("rocket_total_launch_count")
    private Integer total_launch_count;
    @Basic
    @Column(name = "consecutive_successful_launches")
    @JsonProperty("rocket_consecutive_successful_launches")
    private Integer consecutive_successful_launches;
    @Basic
    @Column(name = "successful_launches")
    @JsonProperty("rocket_successful_launches")
    private Integer successful_launches;
    @Basic
    @Column(name = "failed_launches")
    @JsonProperty("rocket_failed_launches")
    private Integer failed_launches;
    @Basic
    @Column(name = "pending_launches")
    @JsonProperty("rocket_pending_launches")
    private Integer pending_launches;
    @Basic
    @Column(name = "attempted_landings")
    @JsonProperty("rocket_attempted_landings")
    private Integer attempted_landings;
    @Basic
    @Column(name = "successful_landings")
    @JsonProperty("rocket_successful_landings")
    private Integer successful_landings;
    @Basic
    @Column(name = "failed_landings")
    @JsonProperty("rocket_failed_landings")
    private Integer failed_landings;
    @Basic
    @Column(name = "consecutive_successful_landings")
    @JsonProperty("rocket_consecutive_successful_landings")
    private Integer consecutive_successful_landings;
    @Basic
    @Column(name = "agency_id")
    @JsonProperty("manufacturer_id")
    private Long agency_id;
    @Basic
    @Column(name = "image_id")
    @JsonProperty("rocket_image_id")
    private Long image_id;

    @Override
    public Object getPrimaryKey() {
        return rocket_conf_id;
    }
}
