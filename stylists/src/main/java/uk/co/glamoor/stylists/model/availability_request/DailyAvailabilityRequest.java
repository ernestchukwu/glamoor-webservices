package uk.co.glamoor.stylists.model.availability_request;

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
    private String stylistId;

    @NotBlank(message = "Service Provider ID must not be blank.")
    private String serviceProviderId;

    @NotNull(message = "Date must not be null.")
    @FutureOrPresent(message = "Date must not be in the past.")
    private LocalDate date;
    
    @Size(min = 1, message = "Timeslots must not be empty.")
    private List<@Valid TimeSlot> timeSlots;
    
	public String getStylistId() {
		return stylistId;
	}
	public void setStylistId(String stylistId) {
		this.stylistId = stylistId;
	}
	public String getServiceProviderId() {
		return serviceProviderId;
	}
	public void setServiceProviderId(String serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public List<TimeSlot> getTimeSlots() {
		return timeSlots;
	}
	public void setTimeSlots(List<TimeSlot> timeSlots) {
		this.timeSlots = timeSlots;
	}
    
}
