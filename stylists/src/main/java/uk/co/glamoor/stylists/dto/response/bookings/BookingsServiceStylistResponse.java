package uk.co.glamoor.stylists.dto.response.bookings;

import lombok.Data;
import uk.co.glamoor.stylists.model.Stylist;

import java.util.ArrayList;
import java.util.List;

@Data
public class BookingsServiceStylistResponse {
	private String id;
	private String alias;
	private String brand;
	private String firstName;
	private String lastName;
	private String timeZone;
	private String logo;
    private String email;
    private Address address;
	private Location location;
    private Phone phone;
	private Currency currency;
    private Double vat;
	private Stylist.CancellationPolicy cancellationPolicy;
	private Integer minAdvanceBookingTimeMinutes;
	private String banner;

	@Data
	public static class Address {
		private String postcode;
		private String address1;
		private String address2;
		private City city;
	}

	@Data
	public static class City {
		private String id;
		private String name;
	}

	@Data
	public static class Location {
		private String type = "Point";
		private List<Double> coordinates = new ArrayList<>();
	}

	@Data
	public static class Phone {
		private String number;
		private String countryCode;
		private String countryISOCode;
	}

	@Data
	public static class Currency {
		private String name;
		private String symbol;
		private String iso;
	}

}
