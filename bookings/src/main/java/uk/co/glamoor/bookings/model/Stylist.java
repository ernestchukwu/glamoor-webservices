package uk.co.glamoor.bookings.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="stylists")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stylist {
	
	@Id
	private String id;
    private String firstName;
    private String lastName;
	private String alias;
    private String brand;
	private String logo;
    private String email;
    private Address address;
    private Location location;
    private String timeZone;
    private Phone phone;
    private Currency currency;
    private Double vat = 0.0;
    private Integer bookingCancellationTimeLimitMinutes;
	private Integer bookingTimeLimitMinutes;
    
    private List<HomeServiceSpecification> homeServiceSpecifications = new ArrayList<>();
    private List<ServiceProvider> serviceProviders = new ArrayList<>();
    private List<StylistServiceSpecification> serviceSpecifications = new ArrayList<>();

}
