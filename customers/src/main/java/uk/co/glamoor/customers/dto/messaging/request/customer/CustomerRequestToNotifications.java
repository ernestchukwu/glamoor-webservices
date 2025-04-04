package uk.co.glamoor.customers.dto.messaging.request.customer;

import lombok.Data;
import uk.co.glamoor.customers.model.Phone;

@Data
public class CustomerRequestToNotifications {
    private String id;
    private Phone phone;
    private String email;
}
