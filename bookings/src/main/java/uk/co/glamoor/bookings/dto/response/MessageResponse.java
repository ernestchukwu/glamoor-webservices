package uk.co.glamoor.bookings.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class MessageResponse {
    private String id;
    private String sender;
    private String message;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String isoTime;
    private boolean containsImage = false;
}
