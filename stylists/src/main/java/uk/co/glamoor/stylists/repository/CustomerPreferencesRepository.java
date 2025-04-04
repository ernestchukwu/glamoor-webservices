package uk.co.glamoor.stylists.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.co.glamoor.stylists.model.CustomerPreferences;

@Repository
public interface CustomerPreferencesRepository extends MongoRepository<CustomerPreferences, String> {
}
