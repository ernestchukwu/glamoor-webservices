package uk.co.glamoor.notifications.model;

import lombok.Data;

@Data
public class Phone {
	
	private String number;
	private String countryCode;
	private String countryISOCode;
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCountryISOCode() {
		return countryISOCode;
	}
	public void setCountryISOCode(String countryISOCode) {
		this.countryISOCode = countryISOCode;
	}
	public String getFullNumber() {
		return countryCode + number;
	}
}
