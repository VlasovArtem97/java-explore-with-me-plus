package ru.practicum.ewm.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeUtil {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Boolean isValidStartAndEnd(LocalDateTime start, LocalDateTime end) {
        return start.isBefore(end);
    }
}
