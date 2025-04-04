package uk.co.glamoor.stylists.model;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Country {
	
	@Size(max = 100, message = "Country name must not exceed 100 characters")
    private String name;

    @Size(min = 2, max = 3, message = "Country code must be 2 or 3 characters")
    private String code;

}
