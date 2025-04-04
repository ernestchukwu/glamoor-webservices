package uk.co.glamoor.customers.dto.messaging.response;

import lombok.Data;

@Data
public class DefaultPaymentMethodResponseFromPayments {

    private String paymentsCustomerId;
    private String paymentMethodId;
}
