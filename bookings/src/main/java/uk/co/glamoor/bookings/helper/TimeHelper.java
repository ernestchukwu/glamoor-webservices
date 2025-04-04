package uk.co.glamoor.bookings.helper;

import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Objects;

@Service
public class TimeHelper {
    public LocalDate toUTCDate(LocalDate localDate, String TimeZone) {
        Objects.requireNonNull(localDate, "LocalDate cannot be null");
        LocalDateTime utcDateTime = toUTCDateTime(localDate.atStartOfDay(), TimeZone);
        return utcDateTime.toLocalDate();
    }

    public boolean isWithinRequestedDay(LocalTime timeToCheck,
                                         LocalDate requestedDate,
                                         String timeZone) {
        LocalDateTime ldt = LocalDateTime.of(requestedDate, timeToCheck);
        ZonedDateTime zdt = ldt.atZone(ZoneId.of(timeZone));

        return zdt.toLocalDate().equals(requestedDate);
    }

    public LocalTime toCallerTimeZone(LocalTime originalTime, String originalTimeZone, String callerTimeZone) {
        LocalDateTime ldt = LocalDateTime.of(LocalDate.now(), originalTime);
        ZonedDateTime originalZdt = ldt.atZone(ZoneId.of(originalTimeZone));

        ZonedDateTime callerZdt = originalZdt.withZoneSameInstant(ZoneId.of(callerTimeZone));

        return callerZdt.toLocalTime();
    }

    private LocalDateTime toUTCDateTime(LocalDateTime localDateTime, String TimeZone) {
        try {
            ZoneId zoneId = ZoneId.of(Objects.requireNonNull(TimeZone, "TimeZone cannot be null"));
            return localDateTime.atZone(zoneId)
                    .withZoneSameInstant(ZoneOffset.UTC)
                    .toLocalDateTime();
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid TimeZone: " + TimeZone, e);
        }
    }

}
