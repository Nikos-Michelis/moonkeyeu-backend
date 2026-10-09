package com.moonkeyeu.etl.api.pipeline.core;

import java.util.function.Function;

public class CompositeKey {

    public static <R> R from(Object first, Object second, Function<String, R> finisher) {
        if (first == null || second == null) {
            return null;
        }
        try {
            return finisher.apply(first.toString() + second);
        } catch (RuntimeException ignored) {
            return null;
        }
    }
}