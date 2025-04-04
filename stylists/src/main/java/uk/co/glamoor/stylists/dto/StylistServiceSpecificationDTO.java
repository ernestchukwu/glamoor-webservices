package uk.co.glamoor.stylists.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class StylistServiceSpecificationDTO extends ServiceSpecificationDTO {
    @Size(max = 50, message = "StylistServiceSpecification ID must not exceed 50 characters")
    private String id;
    private @Valid GlamoorServiceDTO service;
    private List<@Valid AddonSpecificationDTO> addonSpecifications;

}
