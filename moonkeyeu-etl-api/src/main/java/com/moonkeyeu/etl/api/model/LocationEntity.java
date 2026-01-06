package com.moonkeyeu.etl.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "location", schema = "moonkey_db")
@JsonPropertyOrder({"location_id", "name", "country_code", "description", "map_image", "location_timezone", "total_launch_count", "total_landing_count"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "location_id")
    private Long location_id;
    @Basic
    @Column(name = "name")
    @JsonProperty("location_name")
    private String name;
    @Basic
    @Column(name = "description")
    @JsonProperty("location_description")
    private String description;
    @Basic
    @Column(name = "map_image")
    @JsonProperty("location_map_image")
    private String map_image;
    @Basic
    @Column(name = "location_timezone")
    private String location_timezone;
    @Basic
    @Column(name = "total_launch_count")
    @JsonProperty("total_location_launches")
    private Integer total_launch_count;
    @Basic
    @Column(name = "total_landing_count")
    @JsonProperty("total_location_landings")
    private Integer total_landing_count;

    @Override
    public Object getPrimaryKey() {
        return location_id;
    }
}
