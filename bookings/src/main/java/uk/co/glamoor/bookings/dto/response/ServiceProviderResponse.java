package uk.co.glamoor.bookings.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceProviderResponse {

    private String id;
    private String stylistId;
    private Boolean doesHomeService;
    private String firstName;
    private String lastName;
    private String email;
    private StylistResponse.Phone phone;
    private String photo;
    private List<String> services;

}
