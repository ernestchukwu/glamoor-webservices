package uk.co.glamoor.stylists.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.co.glamoor.stylists.model.Addon;

@Repository
public interface AddonRepository extends MongoRepository<Addon, String>{
	boolean existsByName(String name);
}
