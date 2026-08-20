package com.moonkeyeu.core.api.launch.model.landing;

import com.moonkeyeu.core.api.launch.model.launcher.LauncherStage;
import com.moonkeyeu.core.api.launch.model.spacecraft.SpacecraftStage;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "landing", schema = "moonkey_db")
public class Landing {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long landingId;
    @Basic
    @Column(name = "attempt")
    private Boolean attempt;
    @Basic
    @Column(name = "success")
    private String success;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "downrange_distance")
    private Integer downrangeDistance;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landing_zone_id")
    private LandingZone landingZone;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landing_type_id")
    private LandingType landingType;
    @OneToMany(mappedBy = "landing", fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    private Set<LauncherStage> launcherStages;
    @OneToMany(mappedBy = "landing", fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    private Set<SpacecraftStage> spacecraftStages;
}
