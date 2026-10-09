package com.moonkeyeu.etl.api.pipeline.ll2.media;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PendingImage implements StorableImage {

    private final MediaTarget target;
    private final Object id;
    private String imageUrl;

    public PendingImage(MediaTarget target, Object id, String imageUrl) {
        this.target = target;
        this.id = id;
        this.imageUrl = imageUrl;
    }

    @Override
    public String getFolder() {
        return target.getFolder();
    }
}
