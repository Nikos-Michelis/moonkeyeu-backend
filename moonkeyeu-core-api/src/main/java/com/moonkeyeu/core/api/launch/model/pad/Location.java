package com.moonkeyeu.core.api.launch.model.pad;

import com.moonkeyeu.core.api.launch.model.landing.LandingZone;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "location", schema = "moonkey_db")
public class Location {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long locationId;
    @Basic
    @Column(name = "name")
    private String locationName;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "map_image")
    private String mapImage;
    @Basic
    @Column(name = "location_timezone")
    private String locationTimezone;
    @Basic
    @Column(name = "total_launch_count")
    private Integer totalLaunchCount;
    @Basic
    @Column(name = "total_landing_count")
    private Integer totalLandingCount;
    @OneToMany(mappedBy = "location")
    private Set<LaunchPad> launchPads;
    @OneToMany(mappedBy = "location")
    private Set<LandingZone> landingZones;
}
