package com.xmcy.cryptorecommendation.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public final class LocalDateTimeUtils {

    private LocalDateTimeUtils() {
    }

    /**
     * This method convert LocalDate to LocalDateTime
     *
     * @param localDate localDate
     */
    public static LocalDateTime getLocalDateTime(LocalDate localDate) {
        return Objects.isNull(localDate) ? null : LocalDateTime.of(localDate, LocalTime.MIN);
    }
}
