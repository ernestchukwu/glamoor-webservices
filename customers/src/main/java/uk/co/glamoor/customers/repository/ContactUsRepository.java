package uk.co.glamoor.customers.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import uk.co.glamoor.customers.model.ContactUs;

public interface ContactUsRepository extends ReactiveMongoRepository<ContactUs, String> {
}
