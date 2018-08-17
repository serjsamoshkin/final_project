package util;

import util.properties.DateTimePatternsPropertiesReader;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeFormatter {

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            DateTimePatternsPropertiesReader.getInstance().getPropertyValue("date_pattern"));
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(
            DateTimePatternsPropertiesReader.getInstance().getPropertyValue("time_pattern"));

    public static String toString(LocalDate date){
        return DATE_FORMATTER.format(date);
    }

    public static String toString(LocalTime time){
        return TIME_FORMATTER.format(time);
    }

    public static LocalDate toLocalDate(String date){
        return LocalDate.parse(date, DATE_FORMATTER);
    }

    public static LocalTime toLocalTime(String time){
        return LocalTime.parse(time, TIME_FORMATTER);
    }

    public static java.sql.Date toSqlDate(String strDate){
        return java.sql.Date.valueOf(toLocalDate(strDate));
    }

    public static java.sql.Time toSqlTime(String strDate){
        return java.sql.Time.valueOf(toLocalTime(strDate));
    }

    public static java.sql.Date toSqlDate(LocalDate date){
        return java.sql.Date.valueOf(date);
    }

    public static java.sql.Time toSqlTime(LocalTime time){
        return java.sql.Time.valueOf(time);
    }

}
