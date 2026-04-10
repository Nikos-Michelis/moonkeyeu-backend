package com.moonkeyeu.core.api.launch.model.launcher;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

@NamedEntityGraph(
        name = "launcher-launcherStage-status-images",
        attributeNodes = {
                @NamedAttributeNode(value = "launcherStages", subgraph = "launcherStages"),
                @NamedAttributeNode(value = "status", subgraph = "status"),
                @NamedAttributeNode(value = "launcherImages", subgraph = "launcherImages"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "launcherStages",
                        attributeNodes = {
                                @NamedAttributeNode("rocket"),
                                @NamedAttributeNode("launcher"),
                                @NamedAttributeNode("landing"),
                        }
                ),
                @NamedSubgraph(
                        name = "rocket",
                        attributeNodes = {}
                ),
                  @NamedSubgraph(
                        name = "launcher",
                        attributeNodes = {}
                ),
                  @NamedSubgraph(
                        name = "landing",
                        attributeNodes = {}
                ),

                 @NamedSubgraph(
                        name = "status",
                        attributeNodes = {}
                ),
                 @NamedSubgraph(
                        name = "launcherImages",
                        attributeNodes = {}
                ),

        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "launcher", schema = "moonkey_db")
public class Launcher {
    @Id
    @Column(name = "launcher_id")
    @EqualsAndHashCode.Include
    private Long launcherId;
    @Basic
    @Column(name = "details")
    private String details;
    @Basic
    @Column(name = "flight_proven")
    private Boolean flightProven;
    @Basic
    @Column(name = "serial_number")
    private String serialNumber;
    @Basic
    @Column(name = "successful_landings")
    private Integer successfulLandings;
    @Basic
    @Column(name = "attempted_landings")
    private Integer attemptedLandings;
    @Basic
    @Column(name = "flights")
    private Integer flights;
    @Basic
    @Column(name = "last_launch_date")
    private Instant lastLaunchDate;
    @Basic
    @Column(name = "first_launch_date")
    private Instant firstLaunchDate;
    @OneToMany(mappedBy = "launcher", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private Set<LauncherStage> launcherStages;
    @ManyToOne
    @JoinColumn(name = "status_id")
    private LauncherStatus status;
    @OneToMany(mappedBy = "launcher", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private Set<LauncherImages> launcherImages;
}
