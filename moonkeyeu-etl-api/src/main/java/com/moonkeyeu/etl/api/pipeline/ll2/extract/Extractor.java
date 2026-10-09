package com.moonkeyeu.etl.api.pipeline.ll2.extract;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.ll2.Context;
import com.moonkeyeu.etl.api.pipeline.core.RowSink;

/**
 * Pulls rows out of one node of the upstream payload and hands its enriched context to whatever
 * hangs beneath it.
 * <p>
 * Extractors compose: a launch delegates to its rocket, the rocket to its stages, a stage to its
 * crew. Each level emits its own rows and adds the key its children need to {@link Context}. This
 * replaces {@code JsonObjectMapper}, where the same traversal was flattened into twenty-five
 * methods that each re-derived the ancestor ids they needed.
 * <p>
 * Implementations are stateless and safe to share across threads.
 */
@FunctionalInterface
public interface Extractor {

    void extract(JsonNode node, Context context, RowSink sink);
}
