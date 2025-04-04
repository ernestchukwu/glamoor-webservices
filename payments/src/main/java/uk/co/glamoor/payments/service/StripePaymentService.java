package uk.co.glamoor.payments.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.model.SetupIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodListParams;
import com.stripe.param.PaymentMethodListParams.Type;
import com.stripe.param.SetupIntentCreateParams;
import uk.co.glamoor.payments.config.PaymentsConfig;

@Service
public class StripePaymentService {

    public StripePaymentService(PaymentsConfig paymentsConfig) {
        Stripe.apiKey = paymentsConfig.getStripeSecretKey();
    }
	
	public PaymentIntent createPaymentIntent(long amount, 
			String currency, 
			String stripeCustomerId,
			String customerId,
			String bookingId) {
        try {

        	Map<String, String> metadata = new HashMap<>();
        	metadata.put("bookingId", bookingId);
        	metadata.put("customerId", customerId);
        	
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setCustomer(stripeCustomerId)
                .putAllMetadata(metadata)
                .build();

            return PaymentIntent.create(params);
            
        } catch (StripeException e) {
            throw new RuntimeException("Error creating payment intent", e);
        }
    }
	
    public boolean deletePaymentIntent(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            paymentIntent.cancel();
            return true;
        } catch (StripeException e) {
            throw new RuntimeException("Error deleting payment intent", e);
        }
    }
    
    public List<PaymentMethod> getCustomerPaymentCards(String customerId) {
        try {
            PaymentMethodCollection paymentMethods = PaymentMethod.list(
                    PaymentMethodListParams.builder()
                            .setCustomer(customerId)
                            .setType(Type.CARD)
                            .build()
            );

            return paymentMethods.getData().stream()
                    .sorted(Comparator.comparing(PaymentMethod::getCreated).reversed())
                    .collect(Collectors.toList());

        } catch (StripeException e) {
            throw new RuntimeException("Error fetching payment methods for customer " + customerId, e);
        }
    }

    public PaymentMethod attachPaymentCardToCustomer(String customerId, String paymentMethodId) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            PaymentMethodAttachParams params = PaymentMethodAttachParams.builder()
                    .setCustomer(customerId)
                    .build();
            return paymentMethod.attach(params);
        } catch (StripeException e) {
            throw new RuntimeException("Error attaching payment card", e);
        }
    }

    public boolean deletePaymentCard(String paymentMethodId) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            paymentMethod.detach();
            return true;
        } catch (StripeException e) {
            throw new RuntimeException("Error deleting payment card", e);
        }
    }

    public Customer setDefaultPaymentMethod(String customerId, String paymentMethodId) {
        try {
            Customer customer = Customer.retrieve(customerId);
            CustomerUpdateParams params = CustomerUpdateParams.builder()
                    .setInvoiceSettings(
                            CustomerUpdateParams.InvoiceSettings.builder()
                                    .setDefaultPaymentMethod(paymentMethodId)
                                    .build()
                    )
                    .build();
            return customer.update(params);
        } catch (StripeException e) {
            throw new RuntimeException("Error setting default payment method", e);
        }
    }

    public SetupIntent createSetupIntent(String customerId) {
        try {
            SetupIntentCreateParams params = SetupIntentCreateParams.builder()
                    .setCustomer(customerId)
                    .addPaymentMethodType("card")
                    .build();
            return SetupIntent.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Error creating setup intent", e);
        }
    }

    public Customer createCustomer(String uid) {
        try {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setMetadata(Map.of("uid", uid))
                    .build();
            return Customer.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Error creating customer", e);
        }
    }

    public boolean deleteCustomer(String customerId) {
        try {
            Customer customer = Customer.retrieve(customerId);
            customer.delete();
            return true;
        } catch (StripeException e) {
            throw new RuntimeException("Error deleting customer", e);
        }
    }

    public PaymentIntent retrievePaymentIntent(String paymentIntentId) {
        try {
            return PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            throw new RuntimeException("Error retrieving payment intent", e);
        }
    }
}
