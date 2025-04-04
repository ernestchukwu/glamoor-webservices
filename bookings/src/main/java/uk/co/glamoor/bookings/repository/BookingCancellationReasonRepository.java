package uk.co.glamoor.bookings.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.co.glamoor.bookings.model.BookingCancellationReason;

@Repository
public interface BookingCancellationReasonRepository extends MongoRepository<BookingCancellationReason, String> {
    List<BookingCancellationReason> findAll();
}
