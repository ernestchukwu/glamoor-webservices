package uk.co.glamoor.payments.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.co.glamoor.payments.model.Payment;

import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
	void deleteByIntentId(String intentId);
	Optional<Payment> findByBookingId(String BookingId);
}
