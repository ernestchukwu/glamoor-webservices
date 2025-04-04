package uk.co.glamoor.customers.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Phone {

	private String number;
	private String countryCode;
	private String countryISOCode;

}
