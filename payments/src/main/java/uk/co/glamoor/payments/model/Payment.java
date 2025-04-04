package uk.co.glamoor.payments.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "payments")
public class Payment {
	
	@Id
	private String id;
	private String clientSecret;
	private String intentId;
	private long amount;
	private String currency;
	private LocalDateTime timeConfirmed;
	private String bookingId;
	private String customerId;
	private String stripeCustomerId;
	private String chargeId;
	private LocalDateTime timeCreated = LocalDateTime.now();
	private PaymentStatus status = PaymentStatus.PENDING;
	private String paymentMethod;

}
