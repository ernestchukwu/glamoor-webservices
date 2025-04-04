package uk.co.glamoor.stylists.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.co.glamoor.stylists.model.GlamoorService;

@Repository
public interface GlamoorServiceRepository extends MongoRepository<GlamoorService, String> {
    Page<GlamoorService> findByCategoryId(String categoryId, Pageable pageable);

    Page<GlamoorService> findAll(Pageable pageable);
}
