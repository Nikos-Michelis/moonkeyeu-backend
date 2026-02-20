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
@Table(name = "country", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "country_id")
    @EqualsAndHashCode.Include
    private Long country_id;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "alpha_2_code")
    private String alpha_2_code;
    @Basic
    @Column(name = "alpha_3_code")
    private String alpha_3_code;
    @Basic
    @Column(name = "nationality_name")
    private String nationality_name;
    @Basic
    @Column(name = "nationality_name_composed")
    private String nationality_name_composed;

    @Override
    public Object getPrimaryKey() {
        return country_id;
    }
}
