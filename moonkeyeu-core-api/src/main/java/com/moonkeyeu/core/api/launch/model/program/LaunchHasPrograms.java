package com.moonkeyeu.core.api.launch.model.program;

import com.moonkeyeu.core.api.launch.model.launch.Launch;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "launch_has_programs", schema = "moonkey_db")
public class LaunchHasPrograms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private String launchProgramsId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Programs programId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "launch_id")
    private Launch launchId;
}
