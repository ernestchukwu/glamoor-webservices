package uk.co.glamoor.bookings.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {

    private String postcode;
    private String address1;
    private String address2;
    private City city;
}
