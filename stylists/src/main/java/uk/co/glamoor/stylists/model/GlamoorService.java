package uk.co.glamoor.stylists.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "services")
public class GlamoorService {

	@Id
	@Size(max = 50, message = "GlamoorService ID must not exceed 50 characters")
	private String id;
	@Indexed(unique = true)
	@NotBlank
	@NotNull
	@Size(max = 100, message = "GlamoorService name must not exceed 100 characters")
	private String name;
	@Size(max = 200, message = "GlamoorService description must not exceed 200 characters")
	private String description;
	@NotBlank
	@NotNull
	@Size(max = 50, message = "GlamoorService categoryId must not exceed 50 characters")
	private String categoryId;
}
