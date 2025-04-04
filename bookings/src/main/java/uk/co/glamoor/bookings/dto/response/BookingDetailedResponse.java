package uk.co.glamoor.bookings.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import uk.co.glamoor.bookings.enums.BookingStatus;
import uk.co.glamoor.bookings.model.Discount;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingDetailedResponse {
    private String id;
    private String stylistName;
    private String stylistLogo;
    private Integer stylistBookingCancellationTime;
    private String isoTime;
    private BookingStatus status;
    private List<ServiceSpecification> serviceSpecifications;
    private String bookingReference;
    private String address;
    private Boolean homeService;
    private Boolean hasBeenReviewed;
    private String currency;
    private Discount discount;
    private String notes;
    private HomeServiceSpecification homeServiceSpecification;

    @Data
    public static class ServiceSpecification {
        private String name;
        private Double price;
        private Integer duration;
    }

    @Data
    public static class HomeServiceSpecification {
        private String city;
        private Double price;
    }
}
