package uk.co.glamoor.customers.dto.messaging.request.customer;

import lombok.Data;

@Data
public class CustomerRequestToGateway {
    private String id;
    private String uid;
}
