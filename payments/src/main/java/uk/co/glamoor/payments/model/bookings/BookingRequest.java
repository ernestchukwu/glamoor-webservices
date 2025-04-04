package uk.co.glamoor.payments.model.bookings;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BookingRequest {
	
	@NotBlank
	private String customerId;

	@NotBlank
    private String stylistId;

    private List<StylistServiceSpecification> serviceSpecifications = new ArrayList<>();

    @NotBlank
    private String serviceProviderId;

    private String homeServiceCityId;
    
    private Booking.Address address;

    private Booking.Location location;

    @NotNull
    @FutureOrPresent(message = "Date and time must be in the future or present.")
    private LocalDateTime dateTime;
    
    private String notes;

    @EqualsAndHashCode(callSuper = true)
    @Data
	public static class StylistServiceSpecification extends ServiceSpecification {
        private List<ServiceSpecification> addonSpecifications = new ArrayList<>();
    }

    @Data
    public static class ServiceSpecification {
        private String id;
        private String optionId;
    }
}
