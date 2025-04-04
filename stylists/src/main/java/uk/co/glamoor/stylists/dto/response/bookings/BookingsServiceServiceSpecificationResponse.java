package uk.co.glamoor.stylists.dto.response.bookings;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BookingsServiceServiceSpecificationResponse {
    private String note;
    private String description;
    private String image;
    private Double homeServiceAdditionalPrice;
    private Double depositPaymentPercent;
    private Boolean homeServiceAvailable;
    private List<String> terms = new ArrayList<>();
    private List<ServiceSpecificationOption> options = new ArrayList<>();

    @Data
    public static class ServiceSpecificationOption {

        private String id;
        private Double price;
        private Integer durationMinutes;
        private String description;

    }
}
