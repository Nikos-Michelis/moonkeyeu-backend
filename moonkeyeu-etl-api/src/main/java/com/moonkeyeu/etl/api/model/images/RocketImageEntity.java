package com.moonkeyeu.etl.api.model.images;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.ImageEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "rocket_conf_images", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RocketImageEntity implements CsvEntity<Object>, ImageEntity {
    @Id
    @Column(name = "id")
    @JsonProperty("rocket_image_id")
    @EqualsAndHashCode.Include
    private Long image_id;
    @Basic
    @Column(name = "name")
    @JsonProperty("rocket_image_name")
    private String image_name;
    @Basic
    @Column(name = "image_url")
    @JsonProperty("rocket_image_url")
    private String image_url;
    @Basic
    @Column(name = "thumbnail_url")
    @JsonProperty("rocket_thumbnail_url")
    private String thumbnail_url;
    @Basic
    @Column(name = "credit")
    @JsonProperty("rocket_credit")
    private String credit;

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
