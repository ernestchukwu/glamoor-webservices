package uk.co.glamoor.bookings.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "booking_cancellations")
public class BookingCancellation {
	
	@Id
	private String id;
	
	@DBRef
	private Booking booking;
	
	@DBRef
	private BookingCancellationReason bookingCancellationReason;
	
	private String otherReason;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public BookingCancellationReason getBookingCancellationReason() {
		return bookingCancellationReason;
	}

	public void setBookingCancellationReason(BookingCancellationReason bookingCancellationReason) {
		this.bookingCancellationReason = bookingCancellationReason;
	}

	public String getOtherReason() {
		return otherReason;
	}

	public void setOtherReason(String otherReason) {
		this.otherReason = otherReason;
	}

}
