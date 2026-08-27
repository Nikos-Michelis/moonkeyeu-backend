package com.moonkeyeu.etl.api.config;

import com.moonkeyeu.etl.api.pipeline.ll2.media.StorableImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Stand-in {@link StorableImage} for the storage and media tests. */
@Getter
@Builder
@AllArgsConstructor
public class TestEntity implements StorableImage {

    private String id;
    private String value;
    private Boolean active;
    private String imageUrl;
    private String folder;

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
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String getFolder() {
        return folder;
    }
}
