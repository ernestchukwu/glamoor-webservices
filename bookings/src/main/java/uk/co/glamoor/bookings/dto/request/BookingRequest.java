package uk.co.glamoor.bookings.dto.request;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.glamoor.bookings.enums.PaymentOption;

@Data
public class BookingRequest {

    @NotBlank(message = "Customer ID cannot be blank")
    @Size(min = 1, max = 100, message = "Customer ID must be between 1 and 100 characters")
    private String customerId;

    @NotBlank(message = "Stylist ID cannot be blank")
    @Size(min = 1, max = 100, message = "Stylist ID must be between 1 and 100 characters")
    private String stylistId;

    @NotEmpty(message = "At least one service specification is required")
    private List<@Valid StylistServiceSpecification> serviceSpecifications = new ArrayList<>();

    @NotBlank(message = "Service provider ID cannot be blank")
    @Size(min = 1, max = 100, message = "Service provider ID must be between 1 and 100 characters")
    private String serviceProviderId;

    @Size(max = 100, message = "Home service specification ID cannot exceed 100 characters")
    private String homeServiceSpecificationId;

    @Valid
    private Address address;

    @Valid
    private List<Double> location = new ArrayList<>();

    @Valid
    @NotNull(message = "Payment option is required")
    private PaymentOption paymentOption;

    @NotNull(message = "Date and time cannot be null")
    private Instant time;

    @NotBlank(message = "Time zone cannot be blank")
    @Size(min = 1, max = 100, message = "Time zone must be between 1 and 100 characters")
    private String timeZone;

    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    private String notes;

    // Validation group for conditional validation
    public interface HomeServiceValidation {}

    @AssertTrue(message = "Address and location are required for home service")
    private boolean isHomeServiceValid() {
        return homeServiceSpecificationId == null || (address != null && location != null);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class StylistServiceSpecification extends ServiceSpecification {
        @Valid
        private List<ServiceSpecification> addonSpecifications = new ArrayList<>();
    }

    @Data
    public static class ServiceSpecification {
        @NotBlank(message = "Service ID cannot be blank")
        @Size(min = 1, max = 100, message = "Service ID must be between 1 and 100 characters")
        private String id;

        @NotBlank(message = "Option ID cannot be blank")
        @Size(min = 1, max = 100, message = "Option ID must be between 1 and 100 characters")
        private String optionId;
    }

    @Data
    public static class Address {
        @NotBlank(message = "Postcode is required", groups = HomeServiceValidation.class)
        @Size(max = 20, message = "Postcode cannot exceed 20 characters")
        private String postcode;

        @NotBlank(message = "Address line 1 is required", groups = HomeServiceValidation.class)
        @Size(max = 200, message = "Address line 1 cannot exceed 200 characters")
        private String address1;

        @Size(max = 200, message = "Address line 2 cannot exceed 200 characters")
        private String address2;
    }
}
