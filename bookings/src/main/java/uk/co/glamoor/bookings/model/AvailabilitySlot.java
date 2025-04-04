package uk.co.glamoor.bookings.model;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AvailabilitySlot {
	private String stylistId;
	private String serviceProviderId;
	private LocalDate date;
	private Availability.TimeSlot timeSlot;
}
