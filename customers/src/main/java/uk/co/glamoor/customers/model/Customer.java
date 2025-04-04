package uk.co.glamoor.customers.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import uk.co.glamoor.customers.enums.CustomerStatus;

@Data
@Document(collection = "customers")
public class Customer {
	
	@Id
	private String id;
	private String email;
	private String firstName;
	private String lastName;
	private String profilePicture;
	private Phone phone;
	@Indexed(unique = true)
	private String uid;
	private String accountProvider;
	private Boolean emailVerified;
	private Boolean anonymous = false;
	private String paymentCustomerId;
	private PaymentCard lastUsedPaymentCard;
	private String defaultPaymentMethod;
	private String defaultAddress;
	private LocalDateTime dateCreated = LocalDateTime.now();
	private LocalDateTime lastActive = LocalDateTime.now();
	private UserSettings settings = new UserSettings();
	private List<RecentSearch> recentRecentSearches = new ArrayList<>();
	private CustomerStatus status = CustomerStatus.ACTIVE;

}
