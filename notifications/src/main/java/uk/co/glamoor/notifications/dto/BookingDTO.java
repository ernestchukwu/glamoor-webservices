package uk.co.glamoor.notifications.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BookingDTO {

    private String bookingId;
    private User customer;
    private User stylist;
    private User serviceProvider;
    private LocalDateTime time;
    private String timeZone;
    private String referenceNumber;
    private boolean homeService;
    private List<StylistServiceSpecification> stylistServiceSpecifications = new ArrayList<>();
    private String address;


    @Data
    public static class User {
        private String id;
        private String name;
        private String email;
        private String phone;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class StylistServiceSpecification extends ServiceSpecification{
        List<ServiceSpecification> addons = new ArrayList<>();
    }

    @Data
    public static class ServiceSpecification {
        private String service;
        private int duration;
        private double price;
    }

}
