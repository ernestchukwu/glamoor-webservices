package uk.co.glamoor.bookings.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Payment {
    private String paymentId;
    private long amount;
	private LocalDateTime time;
    private String timeZone;
    private String paymentMethod;
}


