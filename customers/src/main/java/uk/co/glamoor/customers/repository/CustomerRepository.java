package uk.co.glamoor.customers.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;
import uk.co.glamoor.customers.model.Customer;


@Repository
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

	Mono<Customer> findByPaymentCustomerId(String paymentCustomerId);

	Mono<Customer> findByUid(String uid);
	Mono<Customer> findByEmail(String email);
}


