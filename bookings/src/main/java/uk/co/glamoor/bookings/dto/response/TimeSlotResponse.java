package uk.co.glamoor.bookings.dto.response;

import java.time.LocalTime;

public record TimeSlotResponse(LocalTime start, LocalTime end) {
}
