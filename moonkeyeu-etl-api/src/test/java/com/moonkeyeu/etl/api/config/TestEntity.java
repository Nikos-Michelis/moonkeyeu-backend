package com.moonkeyeu.etl.api.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.ImageEntity;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestEntity implements CsvEntity<Object>, ImageEntity {

    private String id;
    private String value;
    private Boolean active;
    private String ImageUrl;

    private TestEntity() {
    }

    public TestEntity(String id, String value, Boolean active) {
        this.id = id;
        this.value = value;
        this.active = active;
    }

    public TestEntity(String id, String value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    @Override
    public String getImageUrl() {
        return ImageUrl;
    }

    @Override
    public void setImageUrl(String imageUrl) {
        this.ImageUrl = imageUrl;
    }
}