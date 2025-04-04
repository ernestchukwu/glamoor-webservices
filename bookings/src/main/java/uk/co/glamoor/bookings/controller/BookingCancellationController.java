package uk.co.glamoor.bookings.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotBlank;
import uk.co.glamoor.bookings.model.BookingCancellationReason;
import uk.co.glamoor.bookings.service.BookingCancellationService;

@RestController
@RequestMapping("/api/bookings/cancellation-reasons")
public class BookingCancellationController {

    private final BookingCancellationService bookingCancellationReasonService;

    public BookingCancellationController(BookingCancellationService bookingCancellationReasonService) {
        this.bookingCancellationReasonService = bookingCancellationReasonService;
    }

    @GetMapping
    public ResponseEntity<List<BookingCancellationReason>> getAllCancellationReasons() {
        List<BookingCancellationReason> reasons = bookingCancellationReasonService.getAllCancellationReasons();
        return ResponseEntity.ok(reasons);
    }
    
    @PostMapping
    public ResponseEntity<?> addCancellationReason(
    		@RequestParam @NotBlank(message = "reason must be a string.") String reason) {
    	BookingCancellationReason cancellationReason = new BookingCancellationReason();
    	cancellationReason.setReason(reason);
    	bookingCancellationReasonService.addCancellationReason(cancellationReason);
    	return ResponseEntity.ok("Cancellation reason added.");
    }
}
