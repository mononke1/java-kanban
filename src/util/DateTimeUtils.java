package util;

import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
}
