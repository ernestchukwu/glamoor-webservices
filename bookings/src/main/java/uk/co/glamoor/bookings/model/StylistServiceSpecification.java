package uk.co.glamoor.bookings.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StylistServiceSpecification extends ServiceSpecification {
	
	private String id;
	private GlamoorService service;
	private Integer minAdvanceBookingTimeMinutes;
	private List<AddonSpecification> addonSpecifications = new ArrayList<>();

}

