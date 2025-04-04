package uk.co.glamoor.payments.mapper;

import com.stripe.model.PaymentMethod;

import uk.co.glamoor.payments.model.PaymentCard;

public class PaymentCardMapper {

	public static PaymentCard toPaymentCard(PaymentMethod paymentMethod) {

		if (paymentMethod == null) return null;
    	
		PaymentCard card = new PaymentCard();
		
		card.setPaymentMethodId(paymentMethod.getId());
		card.setPaymentCustomerId(paymentMethod.getCustomer());
		card.setLast4(paymentMethod.getCard().getLast4());
		card.setExpMonth(paymentMethod.getCard().getExpMonth().toString());
		card.setExpYear(paymentMethod.getCard().getExpYear().toString());
		card.setBrand(paymentMethod.getCard().getBrand());
		
		return card;
	}
}
