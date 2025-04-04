package uk.co.glamoor.bookings.model;

import lombok.Data;

@Data
public class Phone {
	
	private String number;
	private String countryCode;
	private String countryISOCode;

	public String getFullNumber() {
		return countryCode + number;
	}
}
