package uk.co.glamoor.stylists.dto.response.bookings;

import lombok.Data;

import java.util.List;

@Data
public class BookingsServiceProviderResponse {

    private String id;
    private String stylistId;
    private Boolean doesHomeService;
    private String firstName;
    private String lastName;
    private String email;
    private BookingsServiceStylistResponse.Phone phone;
    private String photo;
    private List<String> services;

}
