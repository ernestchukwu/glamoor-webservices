package uk.co.glamoor.bookings.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Document(collection = "booking_cancellation_reasons")
public class BookingCancellationReason {

    @Id
    private String id;

    @NotNull
    private String reason;

    private boolean requireMoreDetails = false;
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public boolean isRequireMoreDetails() {
		return requireMoreDetails;
	}

	public void setRequireMoreDetails(boolean requireMoreDetails) {
		this.requireMoreDetails = requireMoreDetails;
	}

}

