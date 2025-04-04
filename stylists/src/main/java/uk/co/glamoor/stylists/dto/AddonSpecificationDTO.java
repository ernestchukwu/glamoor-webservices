package uk.co.glamoor.stylists.dto;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@Data
public class AddonSpecificationDTO extends ServiceSpecificationDTO {
    private String id;
    @Valid
    private AddonDTO addon;

}
