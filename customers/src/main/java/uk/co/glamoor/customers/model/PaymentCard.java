package uk.co.glamoor.customers.model;

import lombok.Data;

@Data
public class PaymentCard {
    private String paymentMethodId;
    private String last4;
    private String expMonth;
    private String expYear;
    private String brand;
}
