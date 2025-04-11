package uk.co.glamoor.bookings.model;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class Payment {
    private String paymentId;
    private long amount;
	private Instant time;
    private String timeZone;
    private String paymentMethod;
}


