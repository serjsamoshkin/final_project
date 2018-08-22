package util.datetime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class TimePlanning {

    private static int timeModulator = 60;

    public static LocalTime plusDuration(LocalTime time, int duration) {
        return time.plusMinutes(duration*timeModulator);
    }

    public static LocalTime minusDuration(LocalTime time, int duration) {
        return time.minusMinutes(duration*timeModulator);
    }

    public static int betweenDuration(LocalTime startTime, LocalTime endTime) {
        return (int) ChronoUnit.MINUTES.between(startTime, endTime)/timeModulator;
    }

    public static LocalTime endOfDay(LocalDate date) {
        return LocalTime.of(18, 0);
    }

    public static LocalTime startOfDay(LocalDate date) {
        return LocalTime.of(9, 0);
    }

    public static int getDurationInHours(int duration) {
        return duration*timeModulator/60;
    }

    public static int getDurationInMinutes(int duration) {
        return duration*timeModulator%60;
    }

    public static int getTimeModulator() {
        return timeModulator;
    }

    public static List<LocalTime> getDailyScheme(LocalDate date){

        List<LocalTime> scheme = new ArrayList<>();
        for (int i = 0; i < TimePlanning.betweenDuration(TimePlanning.startOfDay(date), TimePlanning.endOfDay(date)); i++) {
            scheme.add(TimePlanning.plusDuration(TimePlanning.startOfDay(date), i));
        }

        return scheme;
    }


}
