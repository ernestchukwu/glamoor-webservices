package uk.co.glamoor.bookings.exception;

public class NoAvailableDateException extends RuntimeException {
    public NoAvailableDateException(String message) {
        super(message);
    }
}
