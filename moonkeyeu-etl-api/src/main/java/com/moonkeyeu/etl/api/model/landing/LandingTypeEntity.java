package com.moonkeyeu.etl.api.model.landing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moonkeyeu.etl.api.model.CsvEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "landing_type", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LandingTypeEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "id", nullable = false)
    @JsonProperty("type_id")
    @EqualsAndHashCode.Include
    private Long landing_type_id;
    @Basic
    @Column(name = "name", nullable = false, length = 45)
    @JsonProperty("type_name")
    private String name;
    @Basic
    @Column(name = "abbrev", nullable = false, length = 45)
    @JsonProperty("type_abbrev")
    private String abbrev;
    @Basic
    @Column(name = "description", nullable = false, length = -1)
    @JsonProperty("type_description")
    private String description;

    @Override
    public Object getPrimaryKey() {
        return landing_type_id;
    }
}
