package com.moonkeyeu.etl.api.model.launch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.moonkeyeu.etl.api.model.CsvEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "launch_status", schema = "moonkey_db")
//@JsonPropertyOrder({"status_id", "name", "abbrev", "description"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class LaunchStatusEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "status_id", nullable = false)
    private Long status_id;
    @Basic
    @Column(name = "name")
    @JsonProperty("status_name")
    private String name;
    @Basic
    @Column(name = "abbrev")
    @JsonProperty("status_abbrev")
    private String abbrev;
    @Basic
    @Column(name = "description")
    @JsonProperty("status_description")
    private String description;

    @Override
    public Object getPrimaryKey() {
        return status_id;
    }
}
