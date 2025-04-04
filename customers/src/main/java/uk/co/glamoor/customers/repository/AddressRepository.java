package uk.co.glamoor.customers.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import uk.co.glamoor.customers.model.Address;

@Repository
public interface AddressRepository extends ReactiveMongoRepository<Address, String> {

    Flux<Address> findByCustomer(String customerId);
}
