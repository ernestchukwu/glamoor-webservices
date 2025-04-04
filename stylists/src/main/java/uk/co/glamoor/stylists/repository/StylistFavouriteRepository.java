package uk.co.glamoor.stylists.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.glamoor.stylists.model.StylistFavourite;

@Repository
public interface StylistFavouriteRepository extends MongoRepository<StylistFavourite, String> {

	Optional<StylistFavourite> findByCustomerAndStylist(String customerId, String stylistId);

	@Query("{ 'customer': ?0 }")
	List<StylistFavourite> findFavouritesByCustomerSorted(String customer, Pageable pageable);

	@Query("{ 'customer': ?0 }")
	List<StylistFavourite> findFavouritesByCustomer(String customer, Pageable pageable);
}
