package uk.co.glamoor.bookings.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HomeServiceSpecificationResponse {
    private String id;
    private StylistResponse.City city;
    private Double serviceCharge;
    private Integer serviceDuration;
}
