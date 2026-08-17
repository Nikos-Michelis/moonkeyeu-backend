package com.moonkeyeu.etl.api.model.crew;

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
@Table(name = "role", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long role_id;
    @Basic
    @Column(name = "name")
    @JsonProperty("role")
    private String name;

    @Override
    public Object getPrimaryKey() {
        return role_id;
    }
}
