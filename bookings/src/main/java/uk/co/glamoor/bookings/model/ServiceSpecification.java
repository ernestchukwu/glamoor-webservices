package uk.co.glamoor.bookings.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceSpecification {

	private String note;
	private String description;
	private String image;
	private Double homeServiceAdditionalPrice;
	private Double depositPaymentPercent;
	private Boolean homeServiceAvailable;
	private List<String> terms = new ArrayList<>();
	private List<ServiceSpecificationOption> options = new ArrayList<>();

	@Data
	public static class ServiceSpecificationOption {

		private String id;
		private Double price;
		private Integer durationMinutes;
		private String description;

	}

}
