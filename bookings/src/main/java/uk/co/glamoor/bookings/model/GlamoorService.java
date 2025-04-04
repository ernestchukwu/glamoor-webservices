package uk.co.glamoor.bookings.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GlamoorService {
	
	@NotBlank
	@NotNull
	private String id;
	@NotBlank
	private String name;
	private String description;
	@NotBlank
	@NotNull
	private String categoryId;

}
