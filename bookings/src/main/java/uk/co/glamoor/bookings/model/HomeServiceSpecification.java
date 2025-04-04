package uk.co.glamoor.bookings.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HomeServiceSpecification {
	private String id;
	private City city;
	private Double serviceCharge;
	private Integer serviceDuration;
}
