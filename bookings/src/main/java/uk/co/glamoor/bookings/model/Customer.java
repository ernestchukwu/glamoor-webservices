package uk.co.glamoor.bookings.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="customers")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {
	
	@Id
	private String id;
	private String firstName;
	private String lastName;
	private String email;
    private Phone phone;
	private String profilePicture;
	private String paymentCustomerId;

}
