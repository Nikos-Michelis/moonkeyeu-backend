package com.moonkeyeu.etl.api.model.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.moonkeyeu.etl.api.model.CsvEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "mission_patches", schema = "moonkey_db")
@JsonPropertyOrder({"launch_id", "patch_id", "priority", "name", "image_url"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class MissionPatchesEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "patch_id")
    private Long patch_id;
    @Column(name = "priority")
    private String priority;
    @Basic
    @Column(name = "name")
    @JsonProperty("patch_name")
    private String name;
    @Basic
    @Column(name = "image_url")
    @JsonProperty("patch_image_url")
    private String image_url;
    @Basic
    @Column(name = "launch_id")
    private String launch_id;

    @Override
    public Object getPrimaryKey() {
        return patch_id;
    }
}
