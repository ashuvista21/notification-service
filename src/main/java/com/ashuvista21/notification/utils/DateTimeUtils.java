package com.ashuvista21.notification.utils ;

import java.time.Instant ;
import java.time.ZoneId ;
import java.time.format.DateTimeFormatter ;

public class DateTimeUtils {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a")
                    .withZone(ZoneId.systemDefault()) ;

    public static String format(Instant instant) {

        if (instant == null) {
            return null ;
        }

        return FORMATTER.format(instant) ;
    }
}