package com.moonkeyeu.core.api.launch.model.program;

import com.moonkeyeu.core.api.launch.model.agency.Agencies;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "programs_has_agencies", schema = "moonkey_db")
public class ProgramsHasAgencies {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long programAgencyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Programs programId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agencies agencyId;
}
