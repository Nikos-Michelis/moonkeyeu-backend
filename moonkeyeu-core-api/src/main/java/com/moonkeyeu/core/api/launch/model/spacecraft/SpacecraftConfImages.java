package com.moonkeyeu.core.api.launch.model.spacecraft;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "spacecraft_conf_images", schema = "moonkey_db")
public class SpacecraftConfImages {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long imageId;
    @Basic
    @Column(name = "name")
    private String imageName;
    @Basic
    @Column(name = "image_url")
    private String imageUrl;
    @Basic
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    @Basic
    @Column(name = "credit")
    private String credit;
    @ManyToOne
    @JoinColumn(name = "spacecraft_conf_id")
    private SpacecraftConfiguration spacecraftConfiguration;
}
