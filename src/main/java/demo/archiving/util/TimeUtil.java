package demo.archiving.util;

import java.time.LocalTime;

public class TimeUtil {
    public static Boolean isHotTime() {
        LocalTime runningTime = LocalTime.now();
        LocalTime startTime = LocalTime.of(07,30,00);
        LocalTime endTime = LocalTime.of(22,00,00);
        if ( runningTime.isAfter(startTime) && runningTime.isBefore(endTime)) {
            return true;
        } else return false;
    }
}
