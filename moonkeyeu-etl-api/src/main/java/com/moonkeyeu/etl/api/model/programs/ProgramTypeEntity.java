package com.moonkeyeu.etl.api.model.programs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moonkeyeu.etl.api.model.CsvEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "program_type", schema = "moonkey_db")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgramTypeEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "type_id")
    private Long type_id;
    @Basic
    @Column(name = "name")
    @JsonProperty("type_name")
    private String type_name;

    @Override
    public Object getPrimaryKey() {
        return type_id;
    }
}
