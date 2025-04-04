package uk.co.glamoor.bookings.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import uk.co.glamoor.bookings.model.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String> {

}
