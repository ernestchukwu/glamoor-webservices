package uk.co.glamoor.bookings.model;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Addon {
	
	@NotBlank
	@NotNull
	private String id;
	@NotBlank
	private String name;
	private String description;
}
