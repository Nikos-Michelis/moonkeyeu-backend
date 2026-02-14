package com.moonkeyeu.core.api.launch.model.country;

import com.moonkeyeu.core.api.launch.model.astronaut.Astronaut;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "astronaut_has_country", schema = "moonkey_db")
public class AstronautHasCountry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "astronaut_country_id")
    @EqualsAndHashCode.Include
    private Long astronautCountryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "astronaut_id")
    private Astronaut astronaut;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;
}
