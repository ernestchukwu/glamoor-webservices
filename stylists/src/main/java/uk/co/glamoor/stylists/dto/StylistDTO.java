package uk.co.glamoor.stylists.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import uk.co.glamoor.stylists.model.Address;
import uk.co.glamoor.stylists.model.City;
import uk.co.glamoor.stylists.model.Currency;
import uk.co.glamoor.stylists.model.HomeServiceSpecification;
import uk.co.glamoor.stylists.model.Location;
import uk.co.glamoor.stylists.model.Phone;
import uk.co.glamoor.stylists.model.ServiceProvider;

@Data
public class StylistDTO {
	
	@Size(min = 1, max = 50, message = "ID must be between 1 and 50 characters")
	private String id;
	@Size(min = 1, max = 100, message = "Brand must be between 1 and 100 characters")
	private String brand;
	@Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;
	@Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;
	@Size(max = 255, message = "Logo URL must not exceed 255 characters")
    private String logo;
	@NotNull(message = "Email cannot be null")
	@Email(message = "Email must be valid")
	private String email;
	@Size(min = 1, max = 50, message = "UID must be between 1 and 50 characters")
    private String uid;
	@Size(max = 50, message = "Account provider must not exceed 50 characters")
    private String accountProvider;
	private Boolean emailVerified;
	@Size(max = 50, message = "Alias must not exceed 50 characters")
    private String alias;
	@Size(max = 150, message = "Locality must not exceed 150 characters")
    private String locality;
	private Boolean business;
	private @Valid Phone phone;
	private Boolean phoneVerified;
	@DecimalMin(value = "0.0", message = "Rating must be 0 or higher")
    @DecimalMax(value = "5.0", message = "Rating must be 5 or lower")
    private Double rating;
	@Min(value = 0, message = "Ratings must be 0 or greater")
	private Long ratings;
	private List<String> terms = new ArrayList<>();
	@Size(max = 500, message = "About section must not exceed 500 characters")
    private String about;
	@DecimalMin(value = "0.0", message = "VAT must be 0 or higher")
    private Double vat;
	private @Valid Currency currency;
	private List<@Valid HomeServiceSpecification> homeServiceSpecifications = new ArrayList<>();
	private Boolean homeServiceAvailable;
	private @Valid City city;
	private @Valid Address address;
	private @Valid Location location;
	@Size(max = 255, message = "Banner URL must not exceed 255 characters")
    private String banner;
	@Min(value = 0, message = "Booking cancellation time limit must be 0 or greater")
	private Integer bookingCancellationTimeLimitMinutes;
	@Min(value = 0, message = "Booking time limit must be 0 or greater")
	private Integer bookingTimeLimitMinutes;
	private List<@Valid String> serviceCategories = new ArrayList<>();
	private List<@Valid StylistServiceSpecificationDTO> serviceSpecifications = new ArrayList<>();
	private List<@Valid ServiceProvider> serviceProviders = new ArrayList<>();
	private Boolean favourite;
	private boolean verified;
}
