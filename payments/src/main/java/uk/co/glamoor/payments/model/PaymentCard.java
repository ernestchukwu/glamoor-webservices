package uk.co.glamoor.payments.model;

import lombok.Data;

@Data
public class PaymentCard {
	
	private String paymentMethodId;
	private String paymentCustomerId;
	private String last4;
	private String expMonth;
	private String expYear;
	private String brand;

}
