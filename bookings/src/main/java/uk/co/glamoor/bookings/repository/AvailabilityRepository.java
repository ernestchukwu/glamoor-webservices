package uk.co.glamoor.bookings.repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.co.glamoor.bookings.model.Availability;

@Repository
public interface AvailabilityRepository extends MongoRepository<Availability, String> {
	Optional<Availability> findByStylistIdAndServiceProviderIdAndDateUtc(String stylistId, String serviceProviderId, Instant date);
	List<Availability> findByStylistIdAndServiceProviderIdAndDateUtcBetween(String stylistId, String serviceProviderId, Instant start, Instant end);
	void deleteByStylistIdAndServiceProviderIdAndDateUtc(String stylistId, String serviceProviderId, LocalDate date);
	void deleteByStylistIdAndServiceProviderIdAndDateUtcBetween(String stylistId, String serviceProviderId, LocalDate startDate, LocalDate endDate);

}