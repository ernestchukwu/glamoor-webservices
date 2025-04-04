package uk.co.glamoor.bookings.service;

import java.util.List;

import org.springframework.stereotype.Service;

import uk.co.glamoor.bookings.model.BookingCancellationReason;
import uk.co.glamoor.bookings.repository.BookingCancellationReasonRepository;

@Service
public class BookingCancellationService {

    private final BookingCancellationReasonRepository bookingCancellationReasonRepository;

    public BookingCancellationService(BookingCancellationReasonRepository bookingCancellationReasonRepository) {
        this.bookingCancellationReasonRepository = bookingCancellationReasonRepository;
    }

    public List<BookingCancellationReason> getAllCancellationReasons() {
        return bookingCancellationReasonRepository.findAll();
    }
    
    public void addCancellationReason(BookingCancellationReason reason) {
    	bookingCancellationReasonRepository.save(reason);
    }
}
