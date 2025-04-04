package uk.co.glamoor.stylists.model;

import lombok.Data;

@Data
public class HomeServiceSpecification {
	
	private String id;
	private City city;
	private Double serviceCharge;
    private Integer serviceDuration;

}
