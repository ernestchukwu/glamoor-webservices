package uk.co.glamoor.bookings.model;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "messages")
public class Message {

	private String id;

	private String booking;

    private String sender;

    private String message;

    private LocalDateTime time;

    private boolean containsImage = false;

    private boolean seen = false;

}

