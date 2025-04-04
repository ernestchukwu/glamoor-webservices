package uk.co.glamoor.payments.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.SetupIntent;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import uk.co.glamoor.payments.dto.PaymentIntentConfirmationRequest;
import uk.co.glamoor.payments.dto.PaymentIntentRequest;
import uk.co.glamoor.payments.dto.PaymentIntentResponse;
import uk.co.glamoor.payments.mapper.GlamoorJsonMapper;
import uk.co.glamoor.payments.mapper.PaymentCardMapper;
import uk.co.glamoor.payments.mapper.PaymentMapper;
import uk.co.glamoor.payments.model.Payment;
import uk.co.glamoor.payments.model.PaymentCard;
import uk.co.glamoor.payments.service.MessagingService;
import uk.co.glamoor.payments.service.PaymentService;
import uk.co.glamoor.payments.service.StripePaymentService;

@RestController
@RequestMapping("/api/payments")
@Validated
public class PaymentController {
	
	private final StripePaymentService stripePaymentService;
	private final PaymentService paymentService;
	private final MessagingService messagingService;

	public PaymentController(StripePaymentService stripePaymentService,
                             PaymentService paymentService, MessagingService messagingService) {
		this.stripePaymentService = stripePaymentService;
		this.paymentService = paymentService;
        this.messagingService = messagingService;
    }
	
	@PostMapping("/payment-intent")
	public ResponseEntity<?> createPaymentIntent(
			@RequestBody @Valid PaymentIntentRequest paymentIntentRequest) {
		
		Payment payment = PaymentMapper.toPayment(paymentIntentRequest);

		PaymentIntent paymentIntent = stripePaymentService.createPaymentIntent(
				payment.getAmount(),
				payment.getCurrency(),
				payment.getStripeCustomerId(),
				payment.getCustomerId(),
				payment.getBookingId()
		    );
		
		payment.setClientSecret(paymentIntent.getClientSecret());
		payment.setIntentId(paymentIntent.getId());
		
		paymentService.addPayment(payment);

		PaymentIntentResponse response = new PaymentIntentResponse();
		response.setClientSecret(paymentIntent.getClientSecret());

	    return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{intentId}")
	public ResponseEntity<?> deletePayment(
			@PathVariable @NotBlank String intentId) {
		
		if (stripePaymentService.deletePaymentIntent(intentId)) {
			paymentService.removePayment(intentId);

		    return ResponseEntity.ok("Removed");
		}
		
		return ResponseEntity.internalServerError().build();
	}
	
	@GetMapping("/cards/{stripeCustomerId}")
	public ResponseEntity<List<PaymentCard>> getPaymentMethods(
			@PathVariable @NotBlank String stripeCustomerId) {
		
		return ResponseEntity.ok(stripePaymentService.getCustomerPaymentCards(stripeCustomerId)
				.stream().map(PaymentCardMapper::toPaymentCard)
				.collect(Collectors.toList()));
	}
	
    @PostMapping("/customers/{customerId}/payment-method/{paymentMethodId}")
    public ResponseEntity<PaymentMethod> attachPaymentCardToCustomer(
            @PathVariable String customerId, 
            @PathVariable String paymentMethodId) {
        
    	return ResponseEntity.ok(stripePaymentService
    			.attachPaymentCardToCustomer(customerId, paymentMethodId));
            
    }

    @DeleteMapping("/customers/{customerId}/payment-method/{paymentMethodId}")
    public ResponseEntity<Void> deletePaymentCard(@PathVariable String paymentMethodId) {
        
    	if (stripePaymentService.deletePaymentCard(paymentMethodId))
            return ResponseEntity.noContent().build();
        
    	return ResponseEntity.status(500).build();
    }

    @PatchMapping("/customers/{customerId}/default-payment-method/{paymentMethodId}")
    public ResponseEntity<?> setDefaultPaymentMethod(
            @PathVariable String customerId, 
            @PathVariable String paymentMethodId) {
    	
        if (stripePaymentService.setDefaultPaymentMethod(customerId, paymentMethodId) != null) {

			String strStylistMessage = GlamoorJsonMapper.toJson(PaymentMapper
					.buildCustomerPaymentMethodRequest(customerId, paymentMethodId));

			messagingService.sendMessage(MessagingService.CUSTOMER_EXCHANGE,
					MessagingService.CUSTOMERS_DEFAULT_PAYMENT_UPDATE_ROUTING_KEY, strStylistMessage);

			return ResponseEntity.noContent().build();
		}
        
        return ResponseEntity.status(500).build();
    }

    @GetMapping("/customers/{customerId}/setup-intent")
    public ResponseEntity<String> createSetupIntent(@PathVariable String customerId) {
        
        SetupIntent setupIntent = stripePaymentService.createSetupIntent(customerId);
        return ResponseEntity.ok(setupIntent.getClientSecret());
        
    }

	@PostMapping("/confirm-payment")
	public ResponseEntity<Void> confirmPayment(
			@RequestBody @Valid PaymentIntentConfirmationRequest paymentIntentConfirmationRequest) {

		PaymentIntent paymentIntent = stripePaymentService
				.retrievePaymentIntent(paymentIntentConfirmationRequest.getPaymentIntentId());

		paymentService.confirmPayment(paymentIntentConfirmationRequest, paymentIntent);

		return ResponseEntity.noContent().build();

	}

    @PostMapping("/customers")
    public ResponseEntity<String> createCustomer(@RequestParam String uid) {
    	Customer customer = stripePaymentService.createCustomer(uid);
        return ResponseEntity.ok(customer.getId());
    }

    @DeleteMapping("/customers/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        if (stripePaymentService.deleteCustomer(customerId))
            return ResponseEntity.noContent().build();
        return ResponseEntity.status(500).build();
       
    }
}
