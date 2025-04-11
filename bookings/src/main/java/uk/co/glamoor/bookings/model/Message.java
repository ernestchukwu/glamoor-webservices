package uk.co.glamoor.bookings.model;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "messages")
public class Message {

    @Id
	private String id;

	private String booking;

    private String sender;

    private String message;

    private Instant time;
    private String timeZone;

    private boolean containsImage = false;

    private boolean seen = false;

}

