package com.moonkeyeu.etl.api.model.agency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.moonkeyeu.etl.api.model.CsvEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "agency_type", schema = "moonkey_db")
@JsonPropertyOrder({"agency_type_id", "agency_type"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgencyTypeEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "type_id", nullable = false)
    @JsonProperty("agency_type_id")
    @EqualsAndHashCode.Include
    private Long type_id;

    @Column(name = "name", nullable = false, length = 45)
    @JsonProperty("agency_type")
    private String name;

    @Override
    public Object getPrimaryKey() {
        return type_id;
    }
}
