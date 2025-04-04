package uk.co.glamoor.stylists.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.co.glamoor.stylists.model.ServiceCategory;

@Repository
public interface ServiceCategoryRepository extends MongoRepository<ServiceCategory, String> {
    boolean existsByName(String name);
}
