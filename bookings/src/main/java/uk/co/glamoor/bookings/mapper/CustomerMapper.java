package uk.co.glamoor.bookings.mapper;

import uk.co.glamoor.bookings.dto.response.CustomerResponse;
import uk.co.glamoor.bookings.model.Customer;
import uk.co.glamoor.bookings.model.Phone;

public class CustomerMapper {

	public static Customer anonymise(Customer customer) {

		customer.setPaymentCustomerId("");
		customer.setFirstName("Anonymous");
		customer.setLastName("");
		customer.setEmail("");
		customer.setPhone(null);

		return customer;
	}

	public static Customer toCustomer(CustomerResponse customerResponse) {
		Customer customer = new Customer();

		customer.setId(customerResponse.getId());
		customer.setFirstName(customerResponse.getFirstName());
		customer.setLastName(customerResponse.getLastName());
		customer.setEmail(customerResponse.getEmail());
		customer.setPhone(toPhone(customerResponse.getPhone()));
		customer.setProfilePicture(customerResponse.getProfilePicture());
		customer.setPaymentCustomerId(customerResponse.getPaymentCustomerId());

		return customer;
	}

	public static Phone toPhone(CustomerResponse.Phone customerResponsePhone) {
		if (customerResponsePhone == null) return null;
		Phone phone = new Phone();

		phone.setNumber(customerResponsePhone.getNumber());
		phone.setCountryCode(customerResponsePhone.getCountryCode());
		phone.setCountryISOCode(customerResponsePhone.getCountryISOCode());

		return phone;
	}

}
