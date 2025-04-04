package uk.co.glamoor.bookings.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import uk.co.glamoor.bookings.model.Stylist;

public interface StylistRepository extends MongoRepository<Stylist, String>{
	boolean existsByIdAndServiceProviders_Id(String stylistId, String serviceProviderId);
}
