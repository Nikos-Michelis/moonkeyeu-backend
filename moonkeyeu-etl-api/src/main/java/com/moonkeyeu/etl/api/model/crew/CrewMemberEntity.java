package com.moonkeyeu.etl.api.model.crew;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.moonkeyeu.etl.api.model.CsvEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "crew_member", schema = "moonkey_db")
@JsonPropertyOrder({"crew_member_id", "astronaut_id", "role_id", "crew_group_id", "launch_id"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrewMemberEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    private Long crew_member_id;
    @Basic
    @Column(name = "astronaut_id", nullable = false)
    private Integer astronaut_id;
    @Basic
    @Column(name = "role_id", nullable = false)
    private Integer role_id;
    @Basic
    @Column(name = "spacecraft_stage_id", nullable = false)
    private Integer spacecraft_stage_id;
    @Basic
    @Column(name = "launch_id")
    private String launch_id;

    @Override
    public Object getPrimaryKey() {
        return crew_member_id;
    }
}
