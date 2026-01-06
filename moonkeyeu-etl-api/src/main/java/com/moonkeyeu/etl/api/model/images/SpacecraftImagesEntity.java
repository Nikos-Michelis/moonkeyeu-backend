package com.moonkeyeu.etl.api.model.images;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.ImageEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "spacecraft_conf_images", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpacecraftImagesEntity implements CsvEntity<Object>, ImageEntity {
    @Id
    @Column(name = "image_id")
    @JsonProperty("spacecraft_image_id")
    private Long image_id;
    @Basic
    @Column(name = "image_name")
    @JsonProperty("spacecraft_image_name")
    private String image_name;
    @Basic
    @Column(name = "image_url")
    @JsonProperty("spacecraft_image_url")
    private String image_url;
    @Basic
    @Column(name = "thumbnail_url")
    @JsonProperty("spacecraft_thumbnail_url")
    private String thumbnail_url;
    @Basic
    @Column(name = "credit")
    @JsonProperty("spacecraft_credit")
    private String credit;
    @Column(name = "spacecraft_conf_id")
    private Long spacecraft_conf_id;

    @Override
    public Object getPrimaryKey() {
        return image_id;
    }

    @Override
    public String getImageUrl() {
        return image_url;
    }

    @Override
    public void setImageUrl(String imageUrl) {
        this.image_url = imageUrl;
    }
}
