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
@Table(name = "launch_pad_images", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PadImagesEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "image_id", nullable = false)
    @JsonProperty("pad_image_id")
    @EqualsAndHashCode.Include
    private Long image_id;
    @Basic
    @Column(name = "image_name", nullable = true, length = 255)
    @JsonProperty("pad_image_name")
    private String image_name;
    @Basic
    @Column(name = "image_url", nullable = true, length = 500)
    @JsonProperty("pad_image_url")
    private String image_url;
    @Basic
    @Column(name = "thumbnail_url", nullable = true, length = 500)
    @JsonProperty("pad_thumbnail_url")
    private String thumbnail_url;
    @Basic
    @Column(name = "credit", nullable = true, length = 255)
    @JsonProperty("pad_credit")
    private String credit;
    @Basic
    @Column(name = "launch_pad_id", nullable = false)
    private Long launch_pad_id;

    @Override
    public Object getPrimaryKey() {
        return image_id;
    }
}
