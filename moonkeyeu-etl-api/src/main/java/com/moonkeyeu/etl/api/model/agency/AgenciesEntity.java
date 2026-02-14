package com.moonkeyeu.etl.api.model.agency;

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
@Table(name = "agencies", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgenciesEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "agency_id")
    @EqualsAndHashCode.Include
    private Long agency_id;
    @Basic
    @Column(name = "name")
    @JsonProperty("agency_name")
    private String name;
    @JsonProperty("agency_type_id")
    private Long type_id;
    @Basic
    @Column(name ="featured", columnDefinition = "TINYINT(1)")
    private Boolean featured;
    @Basic
    @Column(name = "abbrev")
    private String abbrev;
    @Basic
    @Column(name = "description")
    @JsonProperty("agency_description")
    private String description;
    @Basic
    @Column(name = "administrator")
    @JsonProperty("agency_administrator")
    private String administrator;
    @Basic
    @Column(name = "founding_year")
    private String founding_year;
    @Basic
    @Column(name = "launchers")
    private String launchers;
    @Basic
    @Column(name = "spacecraft")
    private String spacecraft;
    @Basic
    @Column(name = "total_launch_count")
    private Integer total_launch_count;
    @Basic
    @Column(name = "consecutive_successful_launches")
    private Integer consecutive_successful_launches;
    @Basic
    @Column(name = "successful_launches")
    private Integer successful_launches;
    @Basic
    @Column(name = "failed_launches")
    private Integer failed_launches;
    @Basic
    @Column(name = "pending_launches")
    private Integer pending_launches;
    @Basic
    @Column(name = "consecutive_successful_landings")
    @JsonProperty("agency_consecutive_successful_landings")
    private Integer consecutive_successful_landings;
    @Basic
    @Column(name = "successful_landings")
    @JsonProperty("agency_successful_landings")
    private Integer successful_landings;
    @Basic
    @Column(name = "failed_landings")
    @JsonProperty("agency_failed_landings")
    private Integer failed_landings;
    @Basic
    @Column(name = "attempted_landings")
    @JsonProperty("agency_attempted_landings")
    private Integer attempted_landings;
    @Basic
    @Column(name = "info_url")
    @JsonProperty("agency_info_url")
    private String info_url;
    @Basic
    @Column(name = "wiki_url")
    @JsonProperty("agency_wiki_url")
    private String wiki_url;

    @Override
    public Object getPrimaryKey() {
        return agency_id;
    }
}
