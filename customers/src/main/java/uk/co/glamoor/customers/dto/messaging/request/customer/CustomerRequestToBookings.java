package uk.co.glamoor.customers.dto.messaging.request.customer;

import lombok.Data;
import uk.co.glamoor.customers.model.Phone;

@Data
public class CustomerRequestToBookings {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Phone phone;
    private String profilePicture;
    private String paymentCustomerId;
}
