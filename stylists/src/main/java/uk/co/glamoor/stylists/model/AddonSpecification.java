package uk.co.glamoor.stylists.model;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
@Data
public class AddonSpecification extends ServiceSpecification {

	private String id;
	@Valid
	private Addon addon;

}
