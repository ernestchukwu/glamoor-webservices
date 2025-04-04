package uk.co.glamoor.stylists.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ServiceSpecification {
	
	@Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String image;

    @DecimalMin(value = "0.0", message = "Home service price must be 0 or higher")
    private Double homeServiceAdditionalPrice;

    private Double depositPaymentPercent;

    private Boolean homeServiceAvailable;

    private List<@Size(max = 200, message = "Term must not exceed 200 characters") String> terms = new ArrayList<>();

    private List<@Valid ServiceSpecificationOption> options = new ArrayList<>();

    @Getter
    @Setter
    @Data
    public static class ServiceSpecificationOption {

        private String id;

        @DecimalMin(value = "0.0", message = "Price must be 0 or higher")
        private Double price;

        @Min(value = 0, message = "Duration must be 0 or higher")
        private Integer durationMinutes;

        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;

    }

}
