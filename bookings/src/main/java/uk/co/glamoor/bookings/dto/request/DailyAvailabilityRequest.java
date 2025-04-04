package uk.co.glamoor.bookings.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DailyAvailabilityRequest {
	
	@NotBlank(message = "Stylist ID must not be blank.")
    @Size(min = 1, max = 100)
    private String stylistId;

    @NotBlank(message = "Service Provider ID must not be blank.")
    @Size(min = 1, max = 100)
    private String serviceProviderId;

    @NotNull(message = "Date must not be null.")
    @FutureOrPresent(message = "Date must not be in the past.")
    private LocalDate date;
    
    @Size(min = 1, message = "Timeslots must not be empty.")
    private List<@Valid TimeSlot> timeSlots;

    @NotBlank(message = "Time zone must not be blank.")
    @Size(min = 1, max = 100)
    private String timeZone;

}
