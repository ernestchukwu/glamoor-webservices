package uk.co.glamoor.bookings.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.co.glamoor.bookings.model.BookingCancellation;

@Repository
public interface BookingCancellationRepository extends MongoRepository<BookingCancellation, String> {

}
