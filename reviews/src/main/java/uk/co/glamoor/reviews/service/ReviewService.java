package uk.co.glamoor.reviews.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import uk.co.glamoor.reviews.model.RequestOrder;
import uk.co.glamoor.reviews.config.ReviewsConfig;
import uk.co.glamoor.reviews.model.Rating;
import uk.co.glamoor.reviews.model.ReviewReply;
import uk.co.glamoor.reviews.repository.CustomRatingRepository;
import uk.co.glamoor.reviews.repository.ReviewRepository;

@Service
public class ReviewService {
	
	private final ReviewRepository reviewRepository;
	private final CustomRatingRepository customRatingRepository;
	private final ReviewsConfig reviewsConfig;
	
	public ReviewService (CustomRatingRepository customRatingRepository,
                          ReviewRepository reviewRepository,
                          ReviewsConfig reviewsConfig) {
		this.customRatingRepository = customRatingRepository;
		this.reviewRepository = reviewRepository;
		this.reviewsConfig = reviewsConfig;
	}
	
	public List<Rating> getRatings(String stylistId, Integer rating, 
			RequestOrder requestOrder, int offset) {
		
		return customRatingRepository.getRatings(stylistId, rating,
				requestOrder, offset, reviewsConfig.getReviewRequestBatchSize(), reviewsConfig.getReviewRepliesRequestBatchSize());
	}
	

	public void addRating(Rating rating) {
		if (reviewRepository.existsByBookingIdAndCustomerId(rating.getBookingId(), 
				rating.getCustomer().getId())) {
			throw new RuntimeException("Review already added.");
		}
		reviewRepository.save(rating);
	}
	
	public void deleteRating(String ratingId, String customerId) {
		if (reviewRepository.existsByIdAndCustomerId(ratingId, customerId)) {
			reviewRepository.deleteById(ratingId);
		}
	}
	
	public void addReplyToReview(String ratingId, ReviewReply reply) {
		
		reply.setId(UUID.randomUUID().toString());
		customRatingRepository.addReplyToReview(ratingId, reply);
        
    }
	
	public void deleteReplyFromReview(String ratingId, String replyId) {
		customRatingRepository.deleteReplyFromReview(ratingId, replyId);
	}
	
	public void updateStylistAlias(String styistId, String alias) {
		
	}
	
	public void deleteStylist(String styistId) {
		reviewRepository.deleteByStylistId(styistId);
	}
}
