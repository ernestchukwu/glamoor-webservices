package uk.co.glamoor.bookings.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Phone phone;
    private String profilePicture;
    private String paymentCustomerId;

    @Data
    public static class Phone {
        private String number;
        private String countryCode;
        private String countryISOCode;
    }
}
