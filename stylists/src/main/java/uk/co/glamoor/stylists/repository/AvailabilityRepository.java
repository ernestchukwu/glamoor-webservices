package uk.co.glamoor.stylists.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.co.glamoor.stylists.model.Availability;

@Repository
public interface AvailabilityRepository extends MongoRepository<Availability, String> {
	Optional<Availability> findByStylistIdAndServiceProviderIdAndDate(String stylistId, String serviceProviderId, LocalDate date);
	List<Availability> findByStylistIdAndServiceProviderIdAndDateBetween(String stylistId, String serviceProviderId, LocalDate startDate, LocalDate endDate);
	void deleteByStylistIdAndServiceProviderIdAndDate(String stylistId, String serviceProviderId, LocalDate date);
	void deleteByStylistIdAndServiceProviderIdAndDateBetween(String stylistId, String serviceProviderId, LocalDate startDate, LocalDate endDate);

}