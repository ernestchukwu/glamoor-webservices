package uk.co.glamoor.customers.dto.response;

import lombok.Data;

@Data
public class UpdateCustomerResponse {
    private CustomerResponse customer;
    private String customToken;

    public UpdateCustomerResponse(CustomerResponse customer, String customToken) {
        this.customer = customer;
        this.customToken = customToken;
    }
}
