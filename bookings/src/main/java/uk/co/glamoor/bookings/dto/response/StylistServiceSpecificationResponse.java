package uk.co.glamoor.bookings.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StylistServiceSpecificationResponse extends ServiceSpecificationResponse {

    private String id;
    private GlamoorService service;
    private List<AddonSpecification> addonSpecifications = new ArrayList<>();

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class GlamoorService extends ServiceSpecification {

        private String id;
        private String name;
        private String description;
        private String categoryId;

    }

    @Data
    public static class ServiceSpecification {

        private String note;
        private String description;
        private String image;
        private Double homeServiceAdditionalPrice;
        private Double depositPaymentPercent;
        private Boolean homeServiceAvailable;
        private List<String> terms = new ArrayList<>();
        private List<ServiceSpecificationOption> options = new ArrayList<>();

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class AddonSpecification extends ServiceSpecification {

        private String id;
        private Addon addon;

    }

    @Data
    public static class Addon {
        private String id;
        private String name;
        private String description;
    }
}
