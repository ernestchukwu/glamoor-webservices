package uk.co.glamoor.stylists.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Data
public class StylistServiceSpecification extends ServiceSpecification {
	
	@Size(max = 50, message = "StylistServiceSpecification ID must not exceed 50 characters")
	private String id;
	private @Valid GlamoorService service;
	private Integer minAdvanceBookingTimeMinutes;
	private List<@Valid AddonSpecification> addonSpecifications = new ArrayList<>();


}
