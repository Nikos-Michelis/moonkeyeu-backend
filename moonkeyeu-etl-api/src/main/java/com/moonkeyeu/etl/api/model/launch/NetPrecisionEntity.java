package com.moonkeyeu.etl.api.model.launch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "net_precision")
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetPrecisionEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    private Long net_precision_id;

    @Column(name = "name", length = 45)
    @JsonProperty("net_precision_name")
    private String name;

    @Column(name = "abbrev", length = 45)
    @JsonProperty("net_precision_abbrev")
    private String abbrev;

    @Column(name = "description")
    @JsonProperty("net_precision_description")
    private String description;

    @Override
    public Object getPrimaryKey() {
        return net_precision_id;
    }
}