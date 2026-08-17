package com.moonkeyeu.etl.api.model.spacecraft;

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
@Table(name = "spacecraft_type", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpacecraftTypeEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "id")
    @JsonProperty("spacecraft_type_id")
    @EqualsAndHashCode.Include
    private Long type_id;
    @Basic
    @Column(name = "name")
    @JsonProperty("spacecraft_type")
    private String name;

    @Override
    public Object getPrimaryKey() {
        return type_id;
    }
}
