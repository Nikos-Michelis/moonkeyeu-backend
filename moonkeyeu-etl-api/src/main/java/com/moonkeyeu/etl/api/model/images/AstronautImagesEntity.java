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
@Table(name = "astronaut_images", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AstronautImagesEntity implements CsvEntity<Object>, ImageEntity {
    @Id
    @Column(name = "id")
    @JsonProperty("astronaut_image_id")
    @EqualsAndHashCode.Include
    private Long image_id;
    @Basic
    @Column(name = "name")
    @JsonProperty("astronaut_image_name")
    private String image_name;
    @Basic
    @Column(name = "image_url")
    @JsonProperty("astronaut_image_url")
    private String image_url;
    @Basic
    @Column(name = "thumbnail_url")
    @JsonProperty("astronaut_thumbnail_url")
    private String thumbnail_url;
    @Basic
    @Column(name = "credit")
    @JsonProperty("astronaut_credit")
    private String credit;
    @Basic
    @Column(name = "astronaut_id")
    private Integer astronaut_id;

    @Override
    public String getImageUrl() {
        return image_url;
    }

    @Override
    public void setImageUrl(String imageUrl) {
        this.image_url = imageUrl;
    }

    @Override
    public Object getPrimaryKey() {
        return image_id;
    }
}
