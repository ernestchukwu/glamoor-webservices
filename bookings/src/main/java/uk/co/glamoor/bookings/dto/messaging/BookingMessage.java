package uk.co.glamoor.bookings.dto.messaging;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class BookingMessage {
	
	private String bookingId;
    private User customer;
    private User stylist;
    private User serviceProvider;
    private Instant time;
	private String timeZone;
	private String referenceNumber;
	private boolean homeService;
	private List<StylistServiceSpecification> stylistServiceSpecifications = new ArrayList<>();
	private String address;


	@Data
	public static class User {
		private String id;
		private String firstName;
		private String lastName;
		private String email;
		private String phone;
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class StylistServiceSpecification extends ServiceSpecification{
		List<ServiceSpecification> addons = new ArrayList<>();
	}

	@Data
	public static class ServiceSpecification {
		private String service;
		private int duration;
		private double price;
	}

}
