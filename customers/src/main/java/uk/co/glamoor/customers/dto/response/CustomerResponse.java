package uk.co.glamoor.customers.dto.response;

import java.util.List;

import lombok.Data;
import uk.co.glamoor.customers.model.*;

@Data
public class CustomerResponse {
	
	private String id;
	private String email;
	private String firstName;
	private String lastName;
	private String profilePicture;
	private Phone phone;
	private String uid;
	private String accountProvider;
	private PaymentCard lastUsedPaymentCard;
	private Boolean emailVerified;
	private String paymentCustomerId;
	private String defaultPaymentMethod;
	private String defaultAddress;
	private List<Address> addresses;
	private UserSettings settings;
	private List<RecentSearch> recentRecentSearches;
	private Boolean anonymous;

}
