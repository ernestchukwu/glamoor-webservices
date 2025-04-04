package uk.co.glamoor.customers.mapper;

import uk.co.glamoor.customers.dto.messaging.request.customer.*;
import uk.co.glamoor.customers.dto.request.CustomerRequest;
import uk.co.glamoor.customers.dto.response.CustomerResponse;
import uk.co.glamoor.customers.model.Customer;
import uk.co.glamoor.customers.model.PaymentCard;
import uk.co.glamoor.customers.model.Phone;

public class CustomerMapper {
	
	public static CustomerResponse toCustomerResponse(Customer customer) {

		if (customer == null) return null;
    	
		CustomerResponse customerResponse = new CustomerResponse();
		
		customerResponse.setId(customer.getId());
		customerResponse.setEmail(customer.getEmail());
		customerResponse.setFirstName(customer.getFirstName());
		customerResponse.setLastName(customer.getLastName());
		customerResponse.setProfilePicture(customer.getProfilePicture());
		customerResponse.setPhone(customer.getPhone());
		customerResponse.setUid(customer.getUid());
		customerResponse.setAnonymous(customer.getAnonymous());
		customerResponse.setAccountProvider(customer.getAccountProvider());
		customerResponse.setEmailVerified(customer.getEmailVerified());
		customerResponse.setPaymentCustomerId(customer.getPaymentCustomerId());
		customerResponse.setLastUsedPaymentCard(customer.getLastUsedPaymentCard());
		customerResponse.setDefaultAddress(customer.getDefaultAddress());
		customerResponse.setDefaultPaymentMethod(customer.getDefaultPaymentMethod());
		customerResponse.setRecentRecentSearches(customer.getRecentRecentSearches());
		customerResponse.setSettings(customer.getSettings());
		
		return customerResponse;
	}


	public static Customer toCustomer(CustomerRequest dto) {

		if (dto == null) return null;
    	
		Customer customer = new Customer();
		
		customer.setUid(dto.getUid());
		customer.setFirstName(dto.getFirstName());
		customer.setLastName(dto.getLastName());
		customer.setEmail(dto.getEmail());
		customer.setPhone(toPhone(dto.getPhone()));
		customer.setLastUsedPaymentCard(toPaymentCard(dto.getLastUsedPaymentCard()));
		customer.setAccountProvider(dto.getAccountProvider());
		customer.setProfilePicture(dto.getProfilePicture());
		customer.setEmailVerified(dto.getEmailVerified());
		customer.setSettings(dto.getSettings());
		
		return customer;
	}

	public static Phone toPhone(CustomerRequest.Phone phoneRequest) {
		Phone phone = new Phone();

		phone.setNumber(phoneRequest.number());
		phone.setCountryCode(phoneRequest.countryCode());
		phone.setCountryISOCode(phone.getCountryISOCode());

		return phone;
	}

	public static PaymentCard toPaymentCard(CustomerRequest.PaymentCard paymentCardRequest) {
		PaymentCard paymentCard = new PaymentCard();

		paymentCard.setBrand(paymentCardRequest.getBrand());
		paymentCard.setPaymentMethodId(paymentCardRequest.getPaymentMethodId());
		paymentCard.setExpMonth(paymentCardRequest.getExpMonth());
		paymentCard.setLast4(paymentCardRequest.getLast4());
		paymentCard.setExpYear(paymentCardRequest.getExpYear());

		return paymentCard;
	}

	public static CustomerRequestToBookings toCustomerRequestToBookings(Customer customer) {
		if (customer == null) return null;
		CustomerRequestToBookings customerRequestToBookings = new CustomerRequestToBookings();
		customerRequestToBookings.setId(customer.getId());
		customerRequestToBookings.setEmail(customer.getEmail());
		customerRequestToBookings.setPhone(customer.getPhone());
		customerRequestToBookings.setFirstName(customer.getFirstName());
		customerRequestToBookings.setLastName(customer.getLastName());
		customerRequestToBookings.setProfilePicture(customer.getProfilePicture());
		customerRequestToBookings.setPaymentCustomerId(customer.getPaymentCustomerId());
		return customerRequestToBookings;
	}

	public static CustomerRequestToNotifications toCustomerRequestToNotifications(Customer customer) {
		if (customer == null) return null;
		CustomerRequestToNotifications customerRequestToNotifications = new CustomerRequestToNotifications();
		customerRequestToNotifications.setId(customer.getId());
		customerRequestToNotifications.setEmail(customer.getEmail());
		customerRequestToNotifications.setPhone(customer.getPhone());
		return customerRequestToNotifications;
	}

	public static CustomerRequestToGateway toCustomerRequestToGateway(Customer customer) {
		if (customer == null) return null;
		CustomerRequestToGateway customerRequestToGateway = new CustomerRequestToGateway();
		customerRequestToGateway.setId(customer.getId());
		customerRequestToGateway.setUid(customer.getUid());
		return customerRequestToGateway;
	}
}
