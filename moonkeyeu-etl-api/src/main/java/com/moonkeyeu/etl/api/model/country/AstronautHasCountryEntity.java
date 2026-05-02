package com.moonkeyeu.etl.api.model.country;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moonkeyeu.etl.api.model.CsvEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "astronaut_has_country", schema = "moonkey_db", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"astronaut_id", "country_id"})
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class AstronautHasCountryEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "astronaut_country_id")
    @EqualsAndHashCode.Include
    private long id;
    @Basic
    @Column(name = "astronaut_id")
    private long astronaut_id;
    @Basic
    @Column(name = "country_id")
    private long country_id;

    @Override
    public Object getPrimaryKey() {
        setPrimaryKey();
        return id;
    }

    public void setPrimaryKey() {
        this.id = Long.parseLong(astronaut_id + "" + country_id);
    }

}
