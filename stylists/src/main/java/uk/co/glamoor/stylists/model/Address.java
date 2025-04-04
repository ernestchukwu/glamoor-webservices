package uk.co.glamoor.stylists.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Address {
	
    @Size(max = 20, message = "Postcode must not exceed 20 characters")
    private String postcode;

    @Size(max = 255, message = "Address line 1 must not exceed 255 characters")
    private String address1;

    @Size(max = 255, message = "Address line 2 must not exceed 255 characters")
    private String address2;

    @Valid
    private City city;

    @Size(max = 100, message = "County must not exceed 100 characters")
    private String county;

    @Valid
    private Country country;

}
