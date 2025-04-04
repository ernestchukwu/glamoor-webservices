package uk.co.glamoor.notifications.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import uk.co.glamoor.notifications.model.Phone;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest {
	
	@NotBlank(message="userId must be provided.")
	private String id;
	private String email;
	private Phone phone;

}
