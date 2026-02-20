package com.moonkeyeu.etl.api.model.images;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "agencies_images", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgenciesImagesEntity implements CsvEntity<Object>, ImageEntity {
    @Id
    @Column(name = "image_id")
    @EqualsAndHashCode.Include
    private Long image_id;
    @Basic
    @Column(name = "image_name")
    private String image_name;
    @Basic
    @Column(name = "image_url")
    private String image_url;
    @Basic
    @Column(name = "thumbnail_url")
    private String thumbnail_url;
    @Getter
    @Basic
    @Column(name = "credit")
    private String credit;
    @Basic
    @Column(name = "agency_id")
    private Long agency_id;

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
