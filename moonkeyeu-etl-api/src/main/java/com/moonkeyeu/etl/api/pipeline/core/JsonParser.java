package com.moonkeyeu.etl.api.pipeline.core;

import tools.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.settings.exceptions.JsonParserException;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.node.MissingNode;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Navigation and coercion over the upstream payload.
 * <p>
 * Every accessor is null-tolerant by design: the Launch Library API omits whole sub-objects
 * routinely, and a missing branch should yield a null column, never an exception.
 * <p/>
 */
@Slf4j
public final class JsonParser {

    /** Upstream writes runs of question marks where a value is unknown. */
    private static final Pattern UNKNOWN = Pattern.compile("\\?{2,}");
    private static final String UNKNOWN_VALUE = "Unknown";

    /** Walks {@code path}, returning a missing node rather than null when any segment is absent. */
    public static JsonNode at(JsonNode node, String... path) {
        JsonNode current = node;
        for (String segment : path) {
            if (current == null || current.isNull() || current.isMissingNode()) {
                return MissingNode.getInstance();
            }
            current = current.path(segment);
        }
        return current == null ? MissingNode.getInstance() : current;
    }

    /** True when the node at {@code path} is a usable object or array. */
    public static boolean present(JsonNode node, String... path) {
        JsonNode found = at(node, path);
        return !found.isMissingNode() && !found.isNull() && !found.isEmpty();
    }

    /** Iterable over an array node, empty when the path is absent or not an array. */
    public static Iterable<JsonNode> array(JsonNode node, String... path) {
        JsonNode found = at(node, path);
        return found.isArray() ? found : Collections.emptyList();
    }

    /**
     * String value, cleaned. Blank becomes null; a run of question marks becomes "Unknown".
     */
    public static String text(JsonNode node, String... path) {
        JsonNode found = at(node, path);
        if (!found.isValueNode() || found.isNull()) {
            return null;
        }
        String value = found.asString();
        if (value == null || value.isBlank()) {
            return null;
        }
        value = value.trim();
        return UNKNOWN.matcher(value).matches() ? UNKNOWN_VALUE : value;
    }

    /**
     * String value truncated to {@code maxLength}. Used for the narrow VARCHAR columns —
     * upstream occasionally returns a longer value than the schema allows, and silently trimming
     * beats failing the whole chunk.
     */
    public static String text(int maxLength, JsonNode node, String... path) {
        String value = text(node, path);
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    public static Long id(JsonNode node, String... path) {
        JsonNode found = at(node, path);
        if (found.isNumber()) {
            return found.asLong();
        }
        if (found.isString()) {
            try {
                return Long.parseLong(found.asString().trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    public static Integer integer(JsonNode node, String... path) {
        JsonNode found = at(node, path);
        if (found.isNumber()) {
            return found.asInt();
        }
        if (found.isString()) {
            try {
                return Integer.valueOf(found.asString().trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    public static Double dbl(JsonNode node, String... path) {
        JsonNode found = at(node, path);
        if (found.isNumber()) {
            return found.asDouble();
        }
        if (found.isString()) {
            try {
                return Double.valueOf(found.asString().trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    public static Boolean bool(JsonNode node, String... path) {
        JsonNode found = at(node, path);
        if (found.isBoolean()) {
            return found.booleanValue();
        }
        if (found.isNumber()) {
            return found.asInt() != 0;
        }
        return null;
    }

    /**
     * Parses an ISO-8601 timestamp. Upstream sends offsets ("2024-01-01T12:00:00Z" and
     * "2024-01-01T12:00:00+02:00"); a bare date is accepted too and read as midnight UTC.
     */
    public static Instant instant(JsonNode node, String... path) {
        String value = text(node, path);
        if (value == null) {
            return null;
        }
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException e) {
            throw new JsonParserException("Unable to parse utc datetime: " + value + " caused: " + e.getMessage());
        }
    }

    /**
     * Parses a calendar date. Some fields backed by a DATE column
     */
    public static LocalDate date(JsonNode node, String... path) {
        String value = text(node, path);
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new JsonParserException("Unable to parse date: " + value + " caused: " + e.getMessage());
        }
    }
}