package uk.co.glamoor.bookings.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StylistResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String alias;
    private String brand;
    private String logo;
    private String email;
    private Address address;
    private Location location;
    private String timeZone;
    private Phone phone;
    private Currency currency;
    private Double vat = 0.0;
    private Integer bookingCancellationTimeLimitMinutes;
    private Integer bookingTimeLimitMinutes;

    @Data
    public static class Address {
        private String postcode;
        private String address1;
        private String address2;
        private City city;
    }

    @Data
    public static class City {
        private String id;
        private String name;
    }

    @Data
    public static class Location {
        private String type = "Point";
        private List<Double> coordinates = new ArrayList<>();
    }

    @Data
    public static class Phone {
        private String number;
        private String countryCode;
        private String countryISOCode;
    }

    @Data
    public static class Currency {
        private String name;
        private String symbol;
        private String iso;
    }
}
