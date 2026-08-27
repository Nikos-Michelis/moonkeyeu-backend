package com.moonkeyeu.etl.api.pipeline.ll2;

import java.util.HashMap;
import java.util.Map;

/**
 * Foreign keys inherited from ancestors in the JSON tree.
 * Immutable: {@link #with} returns a new instance, so a sibling branch can never observe a key set
 * by the branch next to it.
 */
public record Context(Map<String, Object> keys) {

    public static final String LAUNCH_ID = "launch_id";
    public static final String ROCKET_ID = "rocket_id";
    public static final String SPACECRAFT_STAGE_ID = "spacecraft_stage_id";

    public Context {
        keys = Map.copyOf(keys);
    }

    public static Context empty() {
        return new Context(Map.of());
    }

    /**
     * Returns a context with {@code key} bound. A null value is ignored, keeping any prior binding.
     */
    public Context with(String key, Object value) {
        if (value == null) {
            return this;
        }
        Map<String, Object> next = new HashMap<>(keys);
        next.put(key, value);
        return new Context(next);
    }

    public <T> T get(String key) {
        return (T) keys.get(key);
    }

    public String launchId() {
        return get(LAUNCH_ID);
    }

    public Long rocketId() {
        return get(ROCKET_ID);
    }

    public Long spacecraftStageId() {
        return get(SPACECRAFT_STAGE_ID);
    }
}