package uk.co.glamoor.stylists.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="stylists")
public class Stylist {
	
	@Id
	private String id;
	private String brand;
	private String firstName;
	private String lastName;
	private String logo;
	private String email;
	private String uid;
	private String timeZone;
	private String accountProvider;
	private Boolean emailVerified;
	private String alias;
	private String locality;
	private Boolean business = false;
	private Phone phone;
	private Boolean phoneVerified;
	private Boolean verified = false;
	private Double rating;
	@Indexed
	private Integer popularity;
	private Long ratings = (long) 0;
	private List<String> terms = new ArrayList<>();
	@Indexed
	private Status status = Status.INACTIVE;
	private String about;
	private Double vat = 0.0;
	private Currency currency;
	private List<HomeServiceSpecification> homeServiceSpecifications = new ArrayList<>();
	private Address address;
	@GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
	private Location location;
	private String banner;
	private CancellationPolicy cancellationPolicy;
	private Integer minAdvanceBookingTimeMinutes;
	private List<String> serviceCategories = new ArrayList<>();
	@Indexed
	private List<StylistServiceSpecification> serviceSpecifications = new ArrayList<>();
	private List<ServiceProvider> serviceProviders = new ArrayList<>();
	private LocalDateTime timeCreated = LocalDateTime.now();
	private LocalDateTime timeUpdated;

//	@Transient
	private Boolean favourite;
//	@Transient
	private Boolean offersHomeService;

	private String fullName() {
		return firstName + " " + lastName;
	}

	public String getDisplayName() {
		if (brand != null && !brand.trim().isEmpty()) {
			return brand;
		}
		if (!fullName().trim().isEmpty()) {
			return fullName();
		}
		return alias;
	}

	@Data
	public static class CancellationPolicy {
		private Double freeCancellationWindowHours;
		private int penaltyPercent;
	}
	
}
