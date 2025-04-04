package uk.co.glamoor.bookings.service;


import org.springframework.stereotype.Service;

import uk.co.glamoor.bookings.exception.EntityNotFoundException;
import uk.co.glamoor.bookings.exception.EntityType;
import uk.co.glamoor.bookings.mapper.CustomerMapper;
import uk.co.glamoor.bookings.model.Customer;
import uk.co.glamoor.bookings.repository.CustomerRepository;

@Service
public class CustomerService {
	
	private final CustomerRepository customerRepository;
	
	public CustomerService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}
	
	public void addCustomer(Customer customer) {
		customerRepository.save(customer);
	}
	
	public void updateCustomer(Customer customer) {
		
		Customer existing = customerRepository.findById(customer.getId())
				.orElseThrow(() -> new EntityNotFoundException(customer.getId(), EntityType.CUSTOMER));

		existing.setFirstName(customer.getFirstName());
		existing.setLastName(customer.getLastName());
		existing.setEmail(customer.getEmail());
		existing.setPhone(customer.getPhone());
		existing.setPaymentCustomerId(customer.getPaymentCustomerId());

		customerRepository.save(existing);
	}
	
	public void anonymiseCustomer(String customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new EntityNotFoundException(customerId, EntityType.CUSTOMER));

		customerRepository.save(CustomerMapper.anonymise(customer));
	}

}
