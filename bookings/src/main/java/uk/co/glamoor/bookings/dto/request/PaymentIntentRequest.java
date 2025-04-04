package uk.co.glamoor.bookings.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentIntentRequest {
	
	@Min(value = 100, message = "amount must be greater than or equal to 100")
	private long amount;
	@NotBlank(message = "currency is required")
	private String currency;
	@NotBlank(message = "bookingId is required")
	private String bookingId;
	@NotBlank(message = "stripeCustomerId is required")
	private String stripeCustomerId;
	@NotBlank(message = "customerId is required")
	private String customerId;
	
}
