package uk.co.glamoor.stylists.model.availability_request;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class MonthlyAvailabilityRequest {
	
	@NotBlank(message = "Stylist ID must not be blank.")
    private String stylistId;

    @NotBlank(message = "Service Provider ID must not be blank.")
    private String serviceProviderId;

    @NotNull(message = "Start date must not be null.")
    @FutureOrPresent(message = "Start date must not be in the past.")
    private LocalDate startDate;

    @NotNull(message = "End date must not be null.")
    @FutureOrPresent(message = "End date must not be in the past.")
    private LocalDate endDate;

    @NotNull(message = "Weekly schedule must not be null.")
    @Size(min = 1, message = "Weekly schedule must have at least one day with time slots.")
    @Valid
    private Map<DayOfWeek, List<@Valid TimeSlot>> weeklySchedule = new HashMap<>();

    @NotNull(message = "Exemptions list must not be null.")
    @Valid
    private List<@Valid Exemption> exemptions = new ArrayList<>();
	
	public void validateDates() {
	    if (startDate == null || endDate == null) {
	        throw new IllegalArgumentException("Start date and end date must not be null.");
	    }
	    
	    if (startDate.isAfter(endDate)) {
	        throw new IllegalArgumentException("Start date must be before or equal to end date.");
	    }

	    if (startDate.isBefore(LocalDate.now())) {
	        throw new IllegalArgumentException("Start date cannot be in the past.");
	    }
	}

    @Data
	public static class Exemption {
		
		@NotNull(message = "Exemption date must not be null.")
		private LocalDate date;
		private boolean exclude;
		private List<@Valid @NotNull TimeSlot> overrideSlots = new ArrayList<>();
		
		public boolean isOverride() {
	        return overrideSlots != null && !overrideSlots.isEmpty();
	    }

	}
}
