package com.lab.management.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateUtils {

    // VIOLATION: Software Portability - No timezone handling
    // All dates should be stored in UTC and converted to user timezone

    /**
     * Get current date/time in UTC
     */
    public static LocalDateTime getNowUTC() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }

    /**
     * Get current date/time (system default - VIOLATION)
     */
    public static LocalDateTime getNow() {
        return LocalDateTime.now();
    }

    /**
     * Convert UTC time to user timezone (VIOLATION: Not implemented yet)
     */
    public static LocalDateTime convertToUserTimezone(LocalDateTime utcTime, String timezone) {
        try {
            ZonedDateTime utcZoned = utcTime.atZone(ZoneId.of("UTC"));
            return utcZoned.withZoneSameInstant(ZoneId.of(timezone)).toLocalDateTime();
        } catch (Exception e) {
            // VIOLATION: Silent failure on invalid timezone
            return utcTime;
        }
    }

    /**
     * Convert local time to UTC
     */
    public static LocalDateTime convertToUTC(LocalDateTime localTime, String timezone) {
        try {
            ZonedDateTime zoned = localTime.atZone(ZoneId.of(timezone));
            return zoned.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        } catch (Exception e) {
            // VIOLATION: Silent failure on invalid timezone
            return localTime;
        }
    }

    /**
     * Format date time as string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        // VIOLATION: Code Sustainability - Hardcoded format
        return dateTime != null ? dateTime.toString() : "";
    }
}
