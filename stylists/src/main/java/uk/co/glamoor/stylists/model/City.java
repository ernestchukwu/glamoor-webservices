package uk.co.glamoor.stylists.model;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class City {
	
	@Size(max = 50, message = "City ID must not exceed 50 characters")
    private String id;

    @Size(max = 100, message = "City name must not exceed 100 characters")
    private String name;
	
}
