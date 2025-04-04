package uk.co.glamoor.stylists.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import uk.co.glamoor.stylists.model.ServiceSpecification;

import java.util.ArrayList;
import java.util.List;

import static uk.co.glamoor.stylists.model.ServiceSpecification.*;


@Data
public class ServiceSpecificationDTO {
    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;

    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String image;

    @DecimalMin(value = "0.0", message = "Home service price must be 0 or higher")
    private Double homeServicePrice;

    private Boolean homeServiceAvailable;

    private Double homeServiceAdditionalPrice;
    private Double depositPaymentPercent;

    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String description;

    private List<ServiceSpecification.ServiceSpecificationOption> options = new ArrayList<>();

    private List<@Size(max = 200, message = "Term must not exceed 200 characters") String> terms;

}
