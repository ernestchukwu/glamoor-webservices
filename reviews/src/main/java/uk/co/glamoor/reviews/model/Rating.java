package uk.co.glamoor.reviews.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Setter
@Getter
@Document(collection="reviews")
public class Rating {
	
	@Id
	private String id;
	private String stylistId;
	private Customer customer;
	private int rating;
	private Review review;
	private String bookingId;
	private boolean verified;
	
	private List<Service> services = new ArrayList<>();
	private LocalDateTime time = LocalDateTime.now();
    private LocalDateTime lastUpdated;

	@Data
	@Setter
	@Getter
	public static class Review {
		
		private String title;
		private String content;
		List<String> images = new ArrayList<>();
		private int relevance;
		private List<ReviewReply> replies;
		
	}
	
	@Setter
    @Getter
    @Data
	public static class Customer {
		
		private String id;
		private String firstName;
		private String lastName;
		private String profilePicture;

    }
	
	@Setter
    @Getter
    @Data
	public static class Service {
		
		@NotBlank
		private String id;
		@NotBlank
		private String name;

    }
}
