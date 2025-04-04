package uk.co.glamoor.reviews.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.co.glamoor.reviews.model.Rating;

@Repository
public interface ReviewRepository extends MongoRepository<Rating, String>{

	boolean existsByBookingIdAndCustomerId(String bookingId, String customerId);
	
	boolean existsByIdAndCustomerId(String id, String customerId);
	
	void deleteByStylistId(String id);
}
