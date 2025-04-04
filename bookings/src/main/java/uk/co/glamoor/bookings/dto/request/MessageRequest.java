package uk.co.glamoor.bookings.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageRequest {
	
	private String id;

	@NotBlank(message = "Sender is required.")
    private String sender;

    @Size(max = 1024, message = "Messages cannot exceed 1024 characters.")
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime time;

    private boolean containsImage = false;

    private boolean seen = false;

}
