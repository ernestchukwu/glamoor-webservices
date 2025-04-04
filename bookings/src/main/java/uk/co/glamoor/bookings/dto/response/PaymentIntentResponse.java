package uk.co.glamoor.bookings.dto.response;

import lombok.Data;

@Data
public class PaymentIntentResponse {
	
	private String paymentIntentId;
	private String clientSecret;
}
