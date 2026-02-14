package com.moonkeyeu.core.api.launch.model.pad;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "launch_pad_images", schema = "moonkey_db")
public class LaunchPadImages {
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
    @ManyToOne
    @JoinColumn(name = "launch_pad_id")
    private LaunchPad launchPad;
}
