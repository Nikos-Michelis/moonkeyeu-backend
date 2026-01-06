package com.moonkeyeu.etl.api.model.pad;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.ImageEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Data
@Getter
@Setter
@Entity
@Table(name = "launch_pad", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LaunchPadEntity implements CsvEntity<Object>, ImageEntity {
    @Id
    @Column(name = "launch_pad_id")
    private Long launch_pad_id;
    @Basic
    @Column(name = "active", columnDefinition = "TINYINT(1)")
    private Boolean active;
    @Basic
    @Column(name = "name", nullable = true, length = 255)
    @JsonProperty("launch_pad_name")
    private String name;
    @Basic
    @Column(name = "description")
    @JsonProperty("launch_pad_description")
    private String description;
    @Basic
    @Column(name = "info_url", nullable = true, length = 255)
    private String info_url;
    @Basic
    @Column(name = "wiki_url", nullable = true, length = 255)
    private String wiki_url;
    @Basic
    @Column(name = "map_url", nullable = true, length = 255)
    private String map_url;
    @Basic
    @Column(name = "latitude")
    private BigDecimal latitude;
    @Basic
    @Column(name = "longitude")
    private BigDecimal longitude;
    @Basic
    @Column(name = "map_image", nullable = true, length = 255)
    private String map_image;
    @Basic
    @Column(name = "total_launch_count", nullable = true)
    @JsonProperty("total_pad_launches")
    private Integer total_launch_count;
    @Basic
    @Column(name = "orbital_launch_attempt_count", nullable = true)
    @JsonProperty("total_orbital_launch_attempts")
    private Integer orbital_launch_attempt_count;
    @Basic
    @Column(name = "agency_id", nullable = true)
    private Long agency_id;
    @Basic
    @Column(name = "location_id")
    private Long location_id;

    @Override
    public Object getPrimaryKey() {
        return launch_pad_id;
    }

    @Override
    public String getImageUrl() {
        return map_image;
    }
    @Override
    public void setImageUrl(String imageUrl) {
        this.map_image = imageUrl;
    }
}
