package uk.co.glamoor.bookings.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceProvider {
	private String id;
	private String stylistId;
	private Boolean doesHomeService;
	private String firstName;
	private String lastName;
	private String email;
	private Phone phone;
	private String photo;
	private List<String> services;
}

