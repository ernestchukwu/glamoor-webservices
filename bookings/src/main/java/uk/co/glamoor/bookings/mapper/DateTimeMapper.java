package uk.co.glamoor.bookings.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeMapper {

    public static LocalDateTime convertToUTCLocalDateTime(LocalDateTime localDateTime, String timeZone) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(timeZone));
        ZonedDateTime utcDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        return utcDateTime.toLocalDateTime();
    }
}
