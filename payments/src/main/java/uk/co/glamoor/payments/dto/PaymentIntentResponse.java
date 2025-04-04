package uk.co.glamoor.payments.dto;

import lombok.Data;

@Data
public class PaymentIntentResponse {
	
	private String paymentIntentId;
	private String clientSecret;
}
