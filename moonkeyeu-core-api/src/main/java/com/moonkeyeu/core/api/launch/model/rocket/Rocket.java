package com.moonkeyeu.core.api.launch.model.rocket;

import com.moonkeyeu.core.api.launch.model.launcher.LauncherStage;
import com.moonkeyeu.core.api.launch.model.spacecraft.SpacecraftStage;
import com.moonkeyeu.core.api.launch.model.launch.Launch;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import java.util.Set;

@NamedEntityGraph(
        name = "rocket-images-agency",
        attributeNodes = {
                @NamedAttributeNode(value = "rocketConfiguration", subgraph = "rocketConfiguration"),
                @NamedAttributeNode(value = "spacecraftStages"),
                @NamedAttributeNode(value = "launcherStages"),
                @NamedAttributeNode(value = "launches"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "rocket",
                        attributeNodes = {
                                @NamedAttributeNode(value = "rocketConfiguration", subgraph = "rocketConfiguration")

                        }
                ),
                @NamedSubgraph(
                        name = "rocketConfiguration",
                        attributeNodes = {
                                @NamedAttributeNode(value = "rocketConfImages"),
                        }
                ),
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "rocket", schema = "moonkey_db")
public class Rocket {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long rocketId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rocket_conf_id")
    private RocketConfiguration rocketConfiguration;
    @OneToMany(mappedBy = "rocket", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private Set<SpacecraftStage> spacecraftStages;
    @OneToMany(mappedBy = "rocket", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private Set<LauncherStage> launcherStages;
    @OneToMany(mappedBy = "rocket", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private Set<Launch> launches;
}
