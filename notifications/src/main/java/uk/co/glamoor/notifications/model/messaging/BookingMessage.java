package uk.co.glamoor.notifications.model.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import uk.co.glamoor.notifications.model.Phone;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingMessage {

	private String id;

	private Customer customer;

	private Stylist stylist;

	private List<StylistServiceSpecification> serviceSpecifications = new ArrayList<>();

	private ServiceProvider serviceProvider;

	private boolean homeService = false;

	private String cancellationReason;

	private Address address;

	private List<Double> coordinates;

	private boolean isReviewed;

	private LocalDateTime time;
	private String timeZone;

	private LocalDateTime timeCreated = LocalDateTime.now();

	private String reference;

	private Location location;

	private String notes;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Customer {

		private String id;
		private String name;
		private String email;
		private Phone phone;
		private String paymentCustomerId;

	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Stylist {

		@Id
		private String id;
		private String name;
		private String alias;
		private String logo;
		private String email;
		private Address address;
		private Phone phone;
		private Double vat = 0.0;
		private Integer bookingCancellationTimeLimitMinutes;

	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ServiceProvider {
		private String id;
		private String name;
		private String email;
		private Phone phone;
		private String photo;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class StylistServiceSpecification extends ServiceSpecification {
		private String id;
		private List<AddonSpecification> addonSpecifications;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class AddonSpecification extends ServiceSpecification {

		private String id;

	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ServiceSpecification {

		private String note;
		private String description;
		private String image;
		private Double homeServiceAdditionalPrice;
		private Double depositPaymentPercent;
		private Boolean homeServiceAvailable;
		private List<String> terms = new ArrayList<>();
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Location {

		@Size(max = 10, message = "Type must not exceed 10 characters")
		private String type = "Point";

		@Size(min = 2, max = 2, message = "Coordinates must contain exactly 2 elements")
		private List<Double> coordinates = new ArrayList<>();

	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Address {

		@Size(max = 20, message = "Postcode must not exceed 20 characters")
		private String postcode;

		@Size(max = 255, message = "Address line 1 must not exceed 255 characters")
		private String address1;

		@Size(max = 255, message = "Address line 2 must not exceed 255 characters")
		private String address2;

	}

}
