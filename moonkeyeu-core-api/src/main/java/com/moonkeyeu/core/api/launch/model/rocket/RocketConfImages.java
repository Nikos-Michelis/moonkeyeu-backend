package com.moonkeyeu.core.api.launch.model.rocket;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "rocket_conf_images", schema = "moonkey_db")
public class RocketConfImages {

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
    @OneToMany(mappedBy = "rocketConfImages")
    @BatchSize(size = 100)
    private Set<RocketConfiguration> rocketConfigurations;
}
