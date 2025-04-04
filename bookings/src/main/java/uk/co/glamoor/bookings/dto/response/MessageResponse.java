package uk.co.glamoor.bookings.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private String id;
    private String sender;
    private String message;
    private LocalDateTime time;
    private Boolean containsImage;
    private Boolean seen;
}
