package uk.co.glamoor.bookings.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
	private final MessageService messageService;
	
	public BookingController(BookingService bookingService,
                             PaymentsAPIService paymentsAPIService,
                             MessageService messageService) {
		
		this.bookingService = bookingService;
        this.paymentsAPIService = paymentsAPIService;
        this.messageService = messageService;
    }

	@GetMapping("/customer/{customerId}")
	public ResponseEntity<List<BookingSummaryResponse>> getBookings(
			@PathVariable @NotBlank(message = "customerId must be a string.") String customerId, 
			@RequestParam(required = false, defaultValue = "0") @PositiveOrZero(message = "offset must be at least 0.") int offset,
			@RequestParam(required = false) BookingStatus status,
			@RequestParam(required = false, defaultValue = "false") boolean homeScreenView) {

		List<Booking> bookings = bookingService.getBookings(offset, customerId, status, homeScreenView);
		return ResponseEntity.ok(bookings.stream().map(BookingMapper::toBookingSummaryResponse).toList());
	}
	
	@GetMapping("/{bookingId}")
	public ResponseEntity<BookingDetailedResponse> getBooking(
			@RequestParam @NotBlank(message = "customerId must be a string.") String customerId,
			@PathVariable @NotBlank(message = "bookingId must be a string.") String bookingId) {
		Booking booking = bookingService.getBooking(customerId, bookingId);
		return ResponseEntity.ok(BookingMapper.toBookingDetailedResponse(booking));
	}
	
	@GetMapping("/{customerId}/{bookingId}/messages")
	public ResponseEntity<List<MessageResponse>> getBookingMessages(
			@PathVariable @NotBlank(message = "customerId must be a string.") String customerId, 
			@PathVariable @NotBlank(message = "bookingId must be a string.") String bookingId,
			@RequestParam @PositiveOrZero(message = "offset must be at least 0.") int offset) {

		Booking booking = bookingService.getBooking(customerId, bookingId);
		if (booking == null) {
			throw new IllegalArgumentException();
		}
		List<Message> messages = messageService.getBookingMessages(bookingId, offset);

		return ResponseEntity.ok(messages.stream().map(MessageMapper::mapToMessageResponse).toList());
	}
	
	@PutMapping("/{customerId}/{bookingId}/seen")
	public ResponseEntity<String> markMessagesAsSeen(
			@PathVariable String customerId,
			@PathVariable String bookingId) {

		try {
			messageService.markMessagesAsSeen(customerId, bookingId);
	        
	        return ResponseEntity.ok("");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("There was a problem updatings messages as seen.");
		}
    }
	
	@PostMapping("/{bookingId}/messages")
	public ResponseEntity<?> sendMessage(
			@PathVariable @NotBlank(message = "bookingId must be a string.") String bookingId,
			@RequestParam(required = false) MultipartFile image,
			@RequestBody @Valid MessageRequest messageRequest){

		Booking booking = bookingService.getBooking(bookingId).orElseThrow(
				() -> new EntityNotFoundException(bookingId, EntityType.BOOKING)
		);
		Message message = MessageMapper.mapToMessage(messageRequest);
		message.setBooking(booking.getId());
		LocalDateTime time = messageService.addMessage(message);

		try {
			if (image != null && !image.isEmpty()) {
				messageService.saveImage(image, message.getId());
				message.setContainsImage(true);
			}
		} catch (IOException e) {
			messageService.deleteMessage(message.getId());
			throw new RuntimeException("There was a problem saving booking message image.", e);
		}
		return ResponseEntity.ok(time);
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
