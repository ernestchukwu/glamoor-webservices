package uk.co.glamoor.bookings.dto.response;

import lombok.Data;

@Data
public class MonthlyAvailabilityResponse {
	private int day;
	private boolean available;

	public MonthlyAvailabilityResponse(int day, boolean available) {
		this.day = day;
		this.available = available;
	}

}
