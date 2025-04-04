package uk.co.glamoor.stylists.model;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Currency {
	
	@Size(max = 50, message = "Currency name must not exceed 50 characters")
	private String name;
	@Size(max = 5, message = "Currency symbol must not exceed 5 characters")
	private String symbol;
	@Size(max = 3, message = "Currency ISO must not exceed 3 characters")
    private String iso;

}
