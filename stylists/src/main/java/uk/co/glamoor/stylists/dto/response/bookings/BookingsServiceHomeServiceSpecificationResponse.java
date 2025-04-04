package uk.co.glamoor.stylists.dto.response.bookings;

import lombok.Data;

@Data
public class BookingsServiceHomeServiceSpecificationResponse {
    private String id;
    private BookingsServiceStylistResponse.City city;
    private Double serviceCharge;
    private Integer serviceDuration;
}
