package uk.co.glamoor.bookings.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import reactor.core.publisher.Mono;
import uk.co.glamoor.bookings.dto.request.MessageRequest;
import uk.co.glamoor.bookings.dto.response.BookingDetailedResponse;
import uk.co.glamoor.bookings.dto.response.BookingSummaryResponse;
import uk.co.glamoor.bookings.dto.response.MessageResponse;
import uk.co.glamoor.bookings.dto.response.PaymentIntentResponse;
import uk.co.glamoor.bookings.dto.request.BookingRequest;
import uk.co.glamoor.bookings.enums.BookingStatus;
import uk.co.glamoor.bookings.exception.EntityNotFoundException;
import uk.co.glamoor.bookings.exception.EntityType;
import uk.co.glamoor.bookings.mapper.BookingMapper;
import uk.co.glamoor.bookings.mapper.MessageMapper;
import uk.co.glamoor.bookings.model.*;
import uk.co.glamoor.bookings.service.BookingService;
import uk.co.glamoor.bookings.service.MessageService;
import uk.co.glamoor.bookings.service.api.PaymentsAPIService;

@RestController
@RequestMapping("/api/bookings")
@Validated
public class BookingController {
	
	private final BookingService bookingService;
	private final PaymentsAPIService paymentsAPIService;
	
	public BookingController(BookingService bookingService,
                             PaymentsAPIService paymentsAPIService) {
		
		this.bookingService = bookingService;
        this.paymentsAPIService = paymentsAPIService;
    }

	@GetMapping
	public ResponseEntity<List<BookingSummaryResponse>> getBookings(
			@RequestHeader(value = "X-User-Id", required = false) String id,
			@RequestParam(required = false, defaultValue = "0") @PositiveOrZero(message = "offset must be at least 0.") int offset,
			@RequestParam(required = false) BookingStatus status,
			@RequestParam(required = false, defaultValue = "false") boolean homeScreenView) {

		List<Booking> bookings = bookingService.getBookings(offset, id, status, homeScreenView);
		return ResponseEntity.ok(bookings.stream().map(BookingMapper::toBookingSummaryResponse).toList());
	}
	
	@GetMapping("/{bookingId}")
	public ResponseEntity<BookingDetailedResponse> getBooking(
			@RequestHeader(value = "X-User-Id", required = false) String id,
			@PathVariable @NotBlank(message = "bookingId must be a string.") String bookingId) {

		Booking booking = bookingService.getBooking(id, bookingId);
		return ResponseEntity.ok(BookingMapper.toBookingDetailedResponse(booking));
	}

	@PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<String> cancelBooking(
            @PathVariable @NotBlank(message = "bookingId must be a string.") String bookingId,
            @RequestParam @NotBlank(message = "customerId must be a string.") String customerId,
            @RequestParam @NotBlank(message = "reason must be a string.") String reason,
			@RequestParam @NotBlank(message = "TimeZone must not be null.") String timeZone,
            @RequestParam(required = false) String reasonDetails) {
		
        bookingService.cancelBooking(bookingId, customerId, reason, reasonDetails, timeZone);
        return ResponseEntity.ok("Booking successfully canceled.");
        
    }

	@PostMapping("/initiate-booking-payment")
	public Mono<ResponseEntity<PaymentIntentResponse>> initiateBookingAndPaymentIntent(
			@RequestBody @Valid BookingRequest bookingRequest) {
		return bookingService.validateBookingRequestAndGenerateBooking(bookingRequest).flatMap(validatedBooking ->
						bookingService.saveBooking(validatedBooking).flatMap( booking ->
				paymentsAPIService.createPaymentIntent(booking.getPaymentIntentRequest(bookingRequest.getPaymentOption()))
		)).map(ResponseEntity::ok);
	}

	@PostMapping("/{bookingId}/payments")
	public ResponseEntity<Void> addPayment(
			@PathVariable @NotBlank(message = "bookingId must be a string.") String bookingId,
			@RequestBody @Valid Payment payment) {

		bookingService.addPayment(bookingId, payment, payment.getTimeZone());

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/is-booking-customer")
	public ResponseEntity<Boolean> isBookingCustomer(
			@RequestParam @NotBlank(message = "customerId must be a string.") String customerId,
			@RequestParam @NotBlank(message = "bookingId must be a string.") String bookingId) {

		return ResponseEntity.ok(bookingService.isBookingCustomer(customerId, bookingId));
	}

}
