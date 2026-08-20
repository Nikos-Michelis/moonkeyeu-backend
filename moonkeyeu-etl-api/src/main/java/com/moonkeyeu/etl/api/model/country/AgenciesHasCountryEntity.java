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
@Table(name = "agencies_has_country", schema = "moonkey_db", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueNumberAndStatus", columnNames = {"agency_id", "country_id"})
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgenciesHasCountryEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;
    @Basic
    @Column(name = "agency_id")
    private Long agency_id;
    @Basic
    @Column(name = "country_id")
    private Long country_id;

    @Override
    public Object getPrimaryKey() {
        setPrimaryKey();
        return id;
    }

    public void setPrimaryKey() {
        this.id = Long.valueOf(agency_id + "" + country_id);
    }
}
