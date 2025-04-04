package uk.co.glamoor.stylists.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Data
@Document(collection="service-addons")
public class Addon {

	@Id
	@Size(max = 50, message = "Addon Id must not exceed 50 characters")
	private String id;
	@Indexed(unique = true)
	@NotBlank
	@NotNull
	@Size(max = 100, message = "Addon name must not exceed 100 characters")
	private String name;
	@Size(max = 200, message = "Addon description must not exceed 200 characters")
	private String description;


}
