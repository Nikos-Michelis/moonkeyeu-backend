package com.moonkeyeu.etl.api.pipeline.ll2.rows;

import com.fasterxml.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.core.JsonParser;

/**
 * One row of any of the seven image tables.
 * <p>
 * {@code agencies_images}, {@code astronaut_images}, {@code launcher_images},
 * {@code launch_pad_images}, {@code spacecraft_conf_images}, {@code programs_images} and
 * {@code rocket_conf_images} are the same five columns plus one owning foreign key, and upstream
 * returns the same object shape for all of them. They share a record; the owning column name lives
 * in the upsert for each table rather than being duplicated across seven near-identical classes.
 * <p>
 * {@code rocket_conf_images} is the one table with no owning key — rocket_configuration points at
 * the image rather than the other way round — so {@code ownerId} is null there.
 */
public record ImageRow(
        Long id,
        String name,
        String imageUrl,
        String thumbnailUrl,
        String credit,
        Long ownerId
) {

    /** Reads an upstream image object. Returns null when the object is absent or has no id. */
    public static ImageRow of(JsonNode imageNode, Long ownerId) {
        Long id = JsonParser.id(imageNode, "id");
        if (id == null) {
            return null;
        }
        return new ImageRow(
                id,
                JsonParser.text(imageNode, "name"),
                JsonParser.text(500, imageNode, "image_url"),
                JsonParser.text(500, imageNode, "thumbnail_url"),
                JsonParser.text(imageNode, "credit"),
                ownerId);
    }

    /** Convenience for the common case of an image nested under a named field. */
    public static ImageRow at(JsonNode parent, Long ownerId, String... path) {
        JsonNode imageNode = JsonParser.at(parent, path);
        if (imageNode.isMissingNode() || imageNode.isNull() || imageNode.isEmpty()) {
            return null;
        }
        return of(imageNode, ownerId);
    }
}