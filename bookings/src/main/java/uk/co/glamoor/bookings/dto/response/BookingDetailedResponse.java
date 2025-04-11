package uk.co.glamoor.bookings.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.glamoor.bookings.enums.PaymentStatus;
import uk.co.glamoor.bookings.model.Address;
import uk.co.glamoor.bookings.model.Booking;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingDetailedResponse extends BookingSummaryResponse {

    private String stylistId;
    private String stylistBanner;
    private String serviceProviderName;
    private String serviceProviderProfilePicture;
    private Integer stylistBookingCancellationTime;
    private List<ServiceSpecification> serviceSpecifications;
    private int unreadMessagesCount = 0;
    private String bookingReference;
    private Address address;
    private Boolean homeService;
    private Boolean hasBeenReviewed;
    private String currency;
    private Double discountAmount;
    private String discountType;
    private String notes;
    private List<Double> location;
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private HomeServiceSpecification homeServiceSpecification;
    private Booking.CancellationPolicy cancellationPolicy;

    @Data
    public static class ServiceSpecification {
        private String name;
        private Double price;
        private Integer duration;
        private String image;
    }

    @Data
    public static class HomeServiceSpecification {
        private String city;
        private Double price;
    }
}
