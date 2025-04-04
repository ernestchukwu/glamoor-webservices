package uk.co.glamoor.bookings.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.co.glamoor.bookings.model.Message;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    Page<Message> findByBooking(String bookingId, Pageable pageable);
}
