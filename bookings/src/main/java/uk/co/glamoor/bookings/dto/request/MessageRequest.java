package uk.co.glamoor.bookings.dto.request;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageRequest {

    @NotBlank(message = "bookingId is required.")
    private String bookingId;

	@NotBlank(message = "Sender is required.")
    private String sender;

    @Size(max = 1024, message = "Messages cannot exceed 1024 characters.")
    private String message;

    private Instant time;

    private boolean containsImage = false;

}
