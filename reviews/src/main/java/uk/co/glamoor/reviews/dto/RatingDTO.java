package uk.co.glamoor.reviews.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import uk.co.glamoor.reviews.model.Rating.Service;

@Setter
@Getter
@Data
public class RatingDTO {
	
	private String id;
	@NotBlank
	private String stylistId;
	@NotNull
	private CustomerDTO customer;
	@Min(value=1)
	@Max(value=5)
	private int rating;
	private ReviewDTO review;
	@NotBlank
	private String bookingId;
	private LocalDateTime time;
	@NotEmpty
	private List<Service> services;


    @Setter
    @Getter
    @Data
	public static class ReviewDTO {
		
		@NotBlank
		private String title;
		@NotBlank
		private String content;
		List<String> images = new ArrayList<>();
		private List<ReplyDTO> replies = new ArrayList<>();


    }
	
	@Setter
    @Getter
    @Data
	public static class ReplyDTO {
		
		private String id;
		@NotBlank
		private String message;
		@NotBlank
		private String replier;
		private LocalDateTime time;


    }
	
	@Setter
    @Getter
    @Data
	public static class CustomerDTO {
		
		@NotBlank
		private String id;
		@NotBlank
		private String firstName;
		@NotBlank
		private String lastName;
		
		private String profilePicture;

    }
}
