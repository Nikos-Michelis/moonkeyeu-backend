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
@Table(name = "programs_images", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgramsImagesEntity implements CsvEntity<Object>, ImageEntity {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long image_id;
    @Basic
    @Column(name = "name")
    private String image_name;
    @Basic
    @Column(name = "image_url")
    private String image_url;
    @Basic
    @Column(name = "thumbnail_url")
    private String thumbnail_url;
    @Basic
    @Column(name = "credit")
    private String credit;
    @Basic
    @Column(name = "program_id")
    private Integer program_id;

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
