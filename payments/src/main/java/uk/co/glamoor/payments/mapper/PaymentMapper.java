package uk.co.glamoor.payments.mapper;

import uk.co.glamoor.payments.dto.BookingPayment;
import uk.co.glamoor.payments.dto.PaymentIntentRequest;
import uk.co.glamoor.payments.model.Payment;
import uk.co.glamoor.payments.model.request.CustomerPaymentMethodRequest;

public class PaymentMapper {
	
	public static Payment toPayment(PaymentIntentRequest request) {

		if (request == null) return null;
    	
		Payment payment = new Payment();
		
		payment.setAmount(request.getAmount());
		payment.setCurrency(request.getCurrency());
		payment.setBookingId(request.getBookingId());
		payment.setCustomerId(request.getCustomerId());
		payment.setStripeCustomerId(request.getStripeCustomerId());
		
		return payment;
	}

	public static CustomerPaymentMethodRequest buildCustomerPaymentMethodRequest
			(String customerId, String paymentMethodId) {
		CustomerPaymentMethodRequest customerPaymentMethodRequest = new CustomerPaymentMethodRequest();

		customerPaymentMethodRequest.setPaymentsCustomerId(customerId);
		customerPaymentMethodRequest.setPaymentMethodId(paymentMethodId);

		return customerPaymentMethodRequest;
	}

	public static BookingPayment toBookingPayment(Payment payment) {
		BookingPayment bookingPayment = new BookingPayment();

		bookingPayment.setPaymentId(payment.getId());
		bookingPayment.setAmount(payment.getAmount());
		bookingPayment.setPaymentMethod(payment.getPaymentMethod());
		bookingPayment.setTime(payment.getTimeConfirmed());

		return bookingPayment;
	}
}
