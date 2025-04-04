package uk.co.glamoor.payments.service;

import com.stripe.model.PaymentIntent;
import org.springframework.stereotype.Service;

import uk.co.glamoor.payments.dto.PaymentIntentConfirmationRequest;
import uk.co.glamoor.payments.exception.PostPaymentConfirmationException;
import uk.co.glamoor.payments.model.Payment;
import uk.co.glamoor.payments.model.PaymentStatus;
import uk.co.glamoor.payments.repository.PaymentRepository;
import uk.co.glamoor.payments.service.api.BookingAPIService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final BookingAPIService bookingAPIService;
	
	public PaymentService(PaymentRepository paymentRepository, BookingAPIService bookingAPIService) {
		this.paymentRepository = paymentRepository;
        this.bookingAPIService = bookingAPIService;
    }
	
	public void addPayment(Payment payment) {
		paymentRepository.save(payment);
	}
	
	public void removePayment(String intentId) {
		paymentRepository.deleteByIntentId(intentId);
	}

	public void confirmPayment(PaymentIntentConfirmationRequest paymentIntentConfirmationRequest,
							   PaymentIntent paymentIntent) {

		if (!paymentIntent.getStatus().equalsIgnoreCase("succeeded")) {
			throw new RuntimeException("Payment confirmation was unsuccessful.");
		}

		if (!paymentIntent.getId().equals(paymentIntentConfirmationRequest.getPaymentIntentId())) {
			throw new PostPaymentConfirmationException("Payment intent mismatch.");
		}

		if (!paymentIntent.getClientSecret().equals(paymentIntentConfirmationRequest.getClientSecret())) {
			throw new PostPaymentConfirmationException("Client secret mismatch.");
		}

		Payment payment = paymentRepository.findByBookingId(paymentIntent.getMetadata().get("bookingId"))
				.orElseThrow(() -> new PostPaymentConfirmationException("Could not find payment with booking id: " +
						paymentIntent.getMetadata().get("bookingId")));

		if (payment.getStatus() == PaymentStatus.CONFIRMED) {return; }

		if (payment.getAmount() != paymentIntent.getAmount()) {
			throw new PostPaymentConfirmationException("Payment amount mismatch.");
		}

		payment.setStatus(PaymentStatus.CONFIRMED);
		payment.setPaymentMethod(paymentIntent.getPaymentMethod());

		Long createdEpoch = paymentIntent.getCreated();

		if (createdEpoch != null) {
			Instant instant = Instant.ofEpochSecond(createdEpoch);
			LocalDateTime confirmedLocalDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
			payment.setTimeConfirmed(confirmedLocalDateTime);
		} else {
			System.out.println("Charge creation time is null.");
		}
		paymentRepository.save(payment);

		bookingAPIService.addPayment(payment).subscribe();
	}
}
