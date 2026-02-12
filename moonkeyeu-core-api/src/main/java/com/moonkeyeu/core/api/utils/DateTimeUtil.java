package com.moonkeyeu.core.api.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private String PATTERN_FORMAT = "dd/MM/yyyy";

    public static LocalDate convertInstantToLocalDate(Instant instant) {
        return instant.atZone(ZoneOffset.UTC).toLocalDate();
    }

    public static DateTimeFormatter getDateTimeFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withZone(ZoneOffset.UTC);
    }

    public static LocalDate getStartOfCurrentYearUtc() {
        return LocalDate.now(ZoneOffset.UTC).withDayOfYear(1);
    }
}
