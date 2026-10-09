package com.moonkeyeu.etl.api.pipeline.ll2.media;

/**
 * An image the storage layer can fetch and file away.
 * <p>
 * Successor to {@code model.ImageEntity}. The contract is the same pair of URL accessors, plus the
 * storage folder — which the strategies previously had to derive by looking the implementing class
 * up in a map. Carrying it here means the storage layer no longer needs to know what kinds of image
 * exist.
 */
public interface StorableImage {
    String getImageUrl();
    void setImageUrl(String imageUrl);
    String getFolder();
}
