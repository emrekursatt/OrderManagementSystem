package com.tr.demo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final String DD_MM_YYY = "dd.MM.yyyy HH:mm:ss";

    public static String format(LocalDateTime localDateTime) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_MM_YYY);
        return localDateTime.format(formatter);
    }
}
