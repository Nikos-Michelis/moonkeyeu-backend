package com.moonkeyeu.etl.api.dto.launch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetPrecision implements Serializable {
    @JsonProperty("id")
    private int net_precision_id;
    @JsonProperty("name")
    private String net_precision_name;
    @JsonProperty("abbrev")
    private String net_precision_abbrev;
    @JsonProperty("description")
    private String net_precision_description;

}