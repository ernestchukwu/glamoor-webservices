package uk.co.glamoor.bookings.repository;

import uk.co.glamoor.bookings.model.Booking;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
	
	Page<Booking> findByStatus(String status, Pageable pageable);
	
	Page<Booking> findByCustomerIdAndStatusOrderByTimeDesc(String customerId, String status, Pageable pageable);

	Page<Booking> findByCustomerIdOrderByTimeDesc(String customerId, Pageable pageable);

	Booking findByCustomerIdAndId(String customerId, String bookingId);
	
	
	Optional<Booking> findById(String id);
	
	boolean existsById(String bookingId);
	
	boolean existsByIdAndCustomerId(String bookingId, String customerId);
	
}


