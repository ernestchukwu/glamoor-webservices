package uk.co.glamoor.customers.model;

import org.springframework.data.annotation.Id;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "customers-addresses")
public class Address {
	
	@Id
	private String id;
	private String customer;
	private String postcode;
	private String address1;
	private String address2;
	private City city;
	private String county;
	private Country country;
}
