package com.moonkeyeu.core.api.launch.model.mission;

import com.moonkeyeu.core.api.launch.model.launch.Launch;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "mission_patches", schema = "moonkey_db")
public class MissionPatches {
    @Id
    @Column(name = "patch_id")
    @EqualsAndHashCode.Include
    private Long patchId;
    @Basic
    @Column(name = "priority")
    private String priority;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "image_url")
    private String imageUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "launch_id")
    private Launch launch;
}
