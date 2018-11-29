package me.exrates.controller.ngcontroller.util;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NgUtil {

    public static final String DATE_PATTERN = "dd.MM.yyyy";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public static Date fromString(String value) {
        return Date.valueOf(LocalDate.parse(value, FORMATTER));
    }

    public static String toString(LocalDate value) {
        return FORMATTER.format(value);
    }

}
