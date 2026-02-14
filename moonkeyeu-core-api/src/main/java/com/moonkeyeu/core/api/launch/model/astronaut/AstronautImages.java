package com.moonkeyeu.core.api.launch.model.astronaut;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "astronaut_images", schema = "moonkey_db")
public class AstronautImages {
    @Id
    @Column(name = "image_id")
    @EqualsAndHashCode.Include
    private Long imageId;
    @Basic
    @Column(name = "image_name")
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "astronaut_id")
    private Astronaut astronaut;
}
