package uk.co.glamoor.reviews.mapper;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import uk.co.glamoor.reviews.dto.RatingDTO;
import uk.co.glamoor.reviews.dto.RatingDTO.CustomerDTO;
import uk.co.glamoor.reviews.dto.RatingDTO.ReplyDTO;
import uk.co.glamoor.reviews.dto.RatingDTO.ReviewDTO;
import uk.co.glamoor.reviews.model.Rating;
import uk.co.glamoor.reviews.model.Rating.Customer;
import uk.co.glamoor.reviews.model.Rating.Review;
import uk.co.glamoor.reviews.model.ReviewReply;

public class RatingMapper {

	public static Rating toRating(RatingDTO dto) {

		if (dto == null) return null;
    	
		Rating rating = new Rating();
		
		rating.setId(dto.getId());
		rating.setStylistId(dto.getStylistId());
		rating.setCustomer(toCustomer(dto.getCustomer()));
		rating.setRating(dto.getRating());
		rating.setReview(toReview(dto.getReview()));
		rating.setBookingId(dto.getBookingId());
		rating.setServices(dto.getServices());
		rating.setTime(LocalDateTime.now());
		
		return rating;
	}
	
	public static RatingDTO toDTO(Rating rating) {

		if (rating == null) return null;
    	
		RatingDTO dto = new RatingDTO();
		
		dto.setId(rating.getId());
		dto.setStylistId(rating.getStylistId());
		dto.setCustomer(toDTO(rating.getCustomer()));
		dto.setRating(rating.getRating());
		dto.setReview(toDTO(rating.getReview()));
		dto.setBookingId(rating.getBookingId());
		dto.setServices(rating.getServices());
		dto.setTime(rating.getTime());
		
		return dto;
	}
	
	public static Review toReview(ReviewDTO dto) {

		if (dto == null) return null;
    	
		Review review = new Review();
		
		review.setTitle(dto.getTitle());
		review.setContent(dto.getContent());
		review.setImages(dto.getImages());
		review.setReplies(dto.getReplies()
				.stream()
				.map(RatingMapper::toReply)
				.collect(Collectors.toList()));
		
		return review;
	}
	
	public static ReviewDTO toDTO(Review review) {

		if (review == null) return null;
    	
		ReviewDTO dto = new ReviewDTO();
		
		dto.setTitle(review.getTitle());
		dto.setContent(review.getContent());
		dto.setImages(review.getImages());
		dto.setReplies(review.getReplies()
				.stream()
				.map(RatingMapper::toDTO)
				.collect(Collectors.toList()));
		
		return dto;
	}
	
	public static ReviewReply toReply(ReplyDTO dto) {

		if (dto == null) return null;

		ReviewReply reply = new ReviewReply();
		
		reply.setId(dto.getId());
		reply.setMessage(dto.getMessage());
		reply.setReplier(dto.getReplier());
		reply.setTime(LocalDateTime.now());
		
		return reply;
	}
	
	public static ReplyDTO toDTO(ReviewReply reply) {

		if (reply == null) return null;
    	
		ReplyDTO dto = new ReplyDTO();
		
		dto.setId(reply.getId());
		dto.setMessage(reply.getMessage());
		dto.setReplier(reply.getReplier());
		dto.setTime(reply.getTime());
		
		return dto;
	}
	
	public static Customer toCustomer(CustomerDTO dto) {

		if (dto == null) return null;
    	
		Customer customer = new Customer();
		
		customer.setId(dto.getId());
		customer.setFirstName(dto.getFirstName());
		customer.setLastName(dto.getLastName());
		customer.setProfilePicture(dto.getProfilePicture());
		
		return customer;
	}
	
	public static CustomerDTO toDTO(Customer customer) {

		if (customer == null) return null;
    	
		CustomerDTO dto = new CustomerDTO();
		
		dto.setId(customer.getId());
		dto.setFirstName(customer.getFirstName());
		dto.setLastName(customer.getLastName());
		dto.setProfilePicture(customer.getProfilePicture());
		
		return dto;
	}
}
