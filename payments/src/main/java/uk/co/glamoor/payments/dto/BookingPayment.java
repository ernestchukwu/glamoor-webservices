package uk.co.glamoor.payments.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingPayment {
    private String paymentId;
    private long amount;
    private LocalDateTime time;
    private String paymentMethod;
}
