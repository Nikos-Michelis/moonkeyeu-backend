package com.moonkeyeu.etl.api.model.programs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moonkeyeu.etl.api.model.CsvEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "programs", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgramsEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long program_id;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "info_url")
    private String info_url;
    @Basic
    @Column(name = "wiki_url")
    private String wiki_url;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "start_date")
    private Instant start_date;
    @Basic
    @Column(name = "type_id")
    private Integer type_id;

    @Override
    public Object getPrimaryKey() {
        return program_id;
    }
}
