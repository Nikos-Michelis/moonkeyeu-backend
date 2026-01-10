package com.moonkeyeu.core.api.launch.model.launch;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "net_precision")
@EqualsAndHashCode(of = "netPrecisionId")
public class NetPrecision {
    @Id
    @Column(name = "net_precision_id", nullable = false)
    private Integer netPrecisionId;
    @Size(max = 45)
    @Column(name = "name", length = 45)
    private String netName;
    @Size(max = 45)
    @Column(name = "abbrev", length = 45)
    private String netAbbrev;
    @Size(max = 255)
    @Column(name = "description")
    private String netDescription;
    @OneToMany(mappedBy = "netPrecision")
    private Set<Launch> launches;
}