package uk.co.glamoor.bookings.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import uk.co.glamoor.bookings.dto.request.PaymentIntentRequest;
import uk.co.glamoor.bookings.enums.BookingStatus;
import uk.co.glamoor.bookings.enums.PaymentOption;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


@Data
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

    private boolean hasBeenReviewed = false;

    private Instant time;
    private String timeZone;

    private LocalDateTime timeCreated = LocalDateTime.now();

    private String bookingReference;

    private BookingStatus status = BookingStatus.PENDING;

    private Currency currency;
    private List<Payment> payments = new ArrayList<>();

    private Discount discount;

    private Location location;
    
    private String notes;

    public LocalDateTime getTimeUtc() {
        return time.atZone(ZoneId.of(timeZone)).toLocalDateTime();
    }

    public String getAddressString() {
        return (address.getAddress1() + ", " + address.getAddress2() + ", " + address.getPostcode())
                .replace(", , ", ", ");
    }

    public Double getTotalAmount() {
        return calculateAmount(false);
    }

    public Double getDepositAmount() {
        return hasDepositPayment() ? calculateAmount(true) : getTotalAmount();
    }

    private Double calculateAmount(boolean isDeposit) {
        double subtotal = calculateServiceSpecificationsTotal(isDeposit);

        if (homeServiceSpecification != null) {
            subtotal += homeServiceSpecification.getServiceCharge();
        }

        return subtotal * (1 + stylist.getVat());
    }

    private double calculateServiceSpecificationsTotal(boolean isDeposit) {
        double total = 0;

        for (StylistServiceSpecification serviceSpec : serviceSpecifications) {
            double depositMultiplier = isDeposit ?
                    (serviceSpec.getDepositPaymentPercent() != null ?
                            serviceSpec.getDepositPaymentPercent() : 1) : 1;

            total += serviceSpec.getOption().getPrice() * depositMultiplier;

            if (homeServiceSpecification != null) {
                total += serviceSpec.getHomeServiceAdditionalPrice() * depositMultiplier;
            }

            total += calculateAddonsTotal(serviceSpec.getAddonSpecifications(), depositMultiplier);
        }

        return total;
    }

    private double calculateAddonsTotal(List<AddonSpecification> addons, double depositMultiplier) {
        return addons.stream()
                .mapToDouble(addon -> addon.getOption().getPrice() * depositMultiplier)
                .sum();
    }

    private boolean hasDepositPayment() {
        for (StylistServiceSpecification serviceDescription : serviceSpecifications) {
            if (serviceDescription.getDepositPaymentPercent() != null) return true;
        }
        return false;
    }
    
    public Integer getTotalDuration() {
    	
    	int total = 0;
    	
    	for (StylistServiceSpecification specification : serviceSpecifications) {
    		total += specification.getOption().getDurationMinutes();

            for (AddonSpecification addonDescription : specification.getAddonSpecifications()) {
                total += addonDescription.getOption().getDurationMinutes();
            }
    	}
    	total += homeService ? homeServiceSpecification.getServiceDuration() : 0;
    	
    	return total;
    }

    public PaymentIntentRequest getPaymentIntentRequest(PaymentOption paymentOption) {

        PaymentIntentRequest paymentIntentRequest = new PaymentIntentRequest();

        paymentIntentRequest.setBookingId(id);
        paymentIntentRequest.setCustomerId(customer.getId());
        paymentIntentRequest.setStripeCustomerId(customer.getPaymentCustomerId());
        paymentIntentRequest.setCurrency(currency.getIso().toLowerCase());

        switch (paymentOption) {
            case FULL -> paymentIntentRequest.setAmount((long) (getTotalAmount() * 100));
            case DEPOSIT -> paymentIntentRequest.setAmount((long) (getDepositAmount() * 100));
        }
        return paymentIntentRequest;
    }

    @Data
    public static class Stylist {

        @Id
        private String id;
        private String firstName;
        private String lastName;
        private String alias;
        private String brand;
        private String logo;
        private String email;
        private Phone phone;
        private Double vat = 0.0;
        private Integer bookingCancellationTimeLimitMinutes;

        private String fullName() {
            return firstName + " " + lastName;
        }

        public String getDisplayName() {
            if (brand != null && !brand.trim().isEmpty()) {
                return brand;
            }
            if (!fullName().trim().isEmpty()) {
                return fullName();
            }
            return alias;
        }
    }

    @Data
    public static class Customer {

        @Id
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private Phone phone;
        private String profilePicture;
        private String paymentCustomerId;

    }

    @Data
    public static class ServiceProvider {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private Phone phone;
        private String profilePicture;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StylistServiceSpecification extends ServiceSpecification {
        private String id;
        private GlamoorService service;
        private List<AddonSpecification> addonSpecifications = new ArrayList<>();
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddonSpecification extends ServiceSpecification {
        private String id;
        private Addon addon;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ServiceSpecification {

        private String note;
        private String description;
        private String image;
        private Double homeServiceAdditionalPrice;
        private Double depositPaymentPercent;
        private Boolean homeServiceAvailable;
        private List<String> terms = new ArrayList<>();
        private ServiceSpecificationOption option;

        @Data
        public static class ServiceSpecificationOption {
            private String id;
            private Double price;
            private Integer durationMinutes;
            private String description;
        }

    }

}

