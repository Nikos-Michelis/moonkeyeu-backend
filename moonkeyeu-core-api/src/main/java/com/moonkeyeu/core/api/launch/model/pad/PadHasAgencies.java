package com.moonkeyeu.core.api.launch.model.pad;

import com.moonkeyeu.core.api.launch.model.agency.Agencies;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "pad_has_agencies", schema = "moonkey_db")
public class PadHasAgencies {
    @Id
    @Column(name = "pad_agency_id")
    @EqualsAndHashCode.Include
    private Long padAgencyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "launch_pad_id")
    private LaunchPad launchPadId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agencies agencyId;
}
