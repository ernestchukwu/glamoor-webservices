package uk.co.glamoor.payments.model.request;

import lombok.Data;

@Data
public class CustomerPaymentMethodRequest {

    private String paymentsCustomerId;
    private String paymentMethodId;
}
