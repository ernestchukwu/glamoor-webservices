package uk.co.glamoor.payments.model.bookings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Setter
@Getter
@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;

    private Customer customer;

    private Stylist stylist;

    private List<StylistServiceSpecification> serviceSpecifications = new ArrayList<>();

    private ServiceProvider serviceProvider;

    private boolean homeService = false;

    private HomeServiceSpecification homeServiceSpecification;

    private Address address;

	private List<Double> coordinates;

    private boolean isReviewed;

    private LocalDateTime time;

    private LocalDateTime timeCreated = LocalDateTime.now();

    private String reference;

    private BookingStatus status = BookingStatus.PENDING;

    private List<Payment> payments;

    private Discount discount;

    private Location location;
    
    private String notes;
    
    public Double getTotalPrice() {
    	
    	double total = 0;
    	
    	for (ServiceSpecification specification : serviceSpecifications) {
    		total += homeService ? specification.getHomeServicePrice() : specification.getOption().getPrice();
    	}
    	total += homeService ? homeServiceSpecification.getServiceCharge() : 0;
    	
    	return total;
    }
    
    public Integer getTotalDuration() {
    	
    	int total = 0;
    	
    	for (ServiceSpecification specification : serviceSpecifications) {
    		total += specification.getOption().getDurationMinutes();
    	}
    	total += homeService ? homeServiceSpecification.getServiceDuration() : 0;
    	
    	return total;
    }


    @Getter
    @Setter
    @Data
    public static class ServiceProvider {
        private String id;
        private String name;
        private String email;
        private Phone phone;
        private String photo;
    }

    @Getter
    @Setter
    @Data
    public static class Stylist {

        private String id;
        private String name;
        private String alias;
        private String logo;
        private String email;
        private String address;
        private Phone phone;
        private Double vat = 0.0;
        private Integer bookingCancellationTimeLimitMinutes;

    }

    @EqualsAndHashCode(callSuper = true)
    @Setter
    @Getter
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StylistServiceSpecification extends ServiceSpecification {
        private String id;
        private GlamoorService service;
        private List<AddonSpecification> addonSpecifications;
    }

    @EqualsAndHashCode(callSuper = true)
    @Setter
    @Getter
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddonSpecification extends ServiceSpecification {

        private String id;
        private Addon addon;

    }

    @Setter
    @Getter
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ServiceSpecification {

        private String note;
        private String description;
        private String image;
        private Double homeServicePrice;
        private Boolean homeServiceAvailable;
        private List<String> terms = new ArrayList<>();
        private ServiceSpecificationOption option;

        @Getter
        @Setter
        @Data
        public static class ServiceSpecificationOption {

            private String id;
            private Double price;
            private Integer durationMinutes;
            private String description;

        }
    }

    @Data
    public static class Location {

        @Size(max = 10, message = "Type must not exceed 10 characters")
        private String type = "Point";

        @Size(min = 2, max = 2, message = "Coordinates must contain exactly 2 elements")
        private List<Double> coordinates = new ArrayList<>();

    }

    @Data
    public static class Address {

        @Size(max = 20, message = "Postcode must not exceed 20 characters")
        private String postcode;

        @Size(max = 255, message = "Address line 1 must not exceed 255 characters")
        private String address1;

        @Size(max = 255, message = "Address line 2 must not exceed 255 characters")
        private String address2;

    }

    @Data
    public static class Phone {

        private String number;
        private String countryCode;
        private String countryISOCode;

        public String getFullNumber() {
            return countryCode + number;
        }
    }

    @Data
    public static class GlamoorService {

        @NotBlank
        @NotNull
        private String id;
        @NotBlank
        private String name;
        private String description;
        @NotBlank
        @NotNull
        private String categoryId;

    }

    @Data
    public static class Addon {

        @NotBlank
        @NotNull
        private String id;
        @NotBlank
        private String name;
        private String description;
    }

    @Data
    public static class HomeServiceSpecification {
        private String id;
        private City city;
        private Double serviceCharge;
        private Integer serviceDuration;
    }

    @Data
    public static class City {
        private String id;
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Customer {

        @Id
        private String id;
        private String name;
        private String email;
        private Phone phone;

    }

    @Data
    public static class Payment {
        private Double amount;
        private String currency;
        private LocalDateTime time;

        private PaymentStatus status = PaymentStatus.PENDING;

        private String paymentMethod;

        private String id;

        public enum PaymentStatus {
            PENDING,
            CONFIRMED,
            PROCESSING_REFUND,
            REFUNDED,
            CANCELED
        }


    }

    @Data
    public static class Discount {

        private Double amount;

        private Type type;

        public static enum Type {
            PERCENT_REDUCTION, AMOUNT_REDUCTION
        }

    }
}

