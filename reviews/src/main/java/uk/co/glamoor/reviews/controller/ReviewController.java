package uk.co.glamoor.reviews.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import reactor.core.publisher.Mono;
import uk.co.glamoor.reviews.dto.RatingDTO;
import uk.co.glamoor.reviews.dto.RatingDTO.ReplyDTO;
import uk.co.glamoor.reviews.mapper.RatingMapper;
import uk.co.glamoor.reviews.model.Rating;
import uk.co.glamoor.reviews.model.RatingsSummary;
import uk.co.glamoor.reviews.model.RequestOrder;
import uk.co.glamoor.reviews.model.ReviewReply;
import uk.co.glamoor.reviews.repository.CustomRatingRepository;
import uk.co.glamoor.reviews.service.ReviewReplyService;
import uk.co.glamoor.reviews.service.ReviewService;
import uk.co.glamoor.reviews.service.api.BookingService;

@RestController
@RequestMapping("/api/reviews")
@Validated
public class ReviewController {
	
	private final ReviewService reviewService;
	private final BookingService bookingService;
    private final ReviewReplyService reviewReplyService;
	private final CustomRatingRepository customRatingRepository;
	
	public ReviewController(ReviewService reviewService,
                            BookingService bookingService, ReviewReplyService reviewReplyService,
                            CustomRatingRepository customRatingRepository) {
		this.reviewService = reviewService;
		this.bookingService = bookingService;
        this.reviewReplyService = reviewReplyService;
        this.customRatingRepository = customRatingRepository;
	}
	
	@GetMapping("/{stylistId}")
	public ResponseEntity<List<Rating>> getReviews(
			@PathVariable @NotBlank String stylistId,
			@RequestParam(defaultValue = "0") @Min(value=0) int offset,
			@RequestParam(required = false) @Min(value=1) @Max(value=5) Integer rating,
			@RequestParam(defaultValue = "DEFAULT") RequestOrder requestOrder) {
		
		List<Rating> ratings = reviewService.getRatings(stylistId, rating, 
				requestOrder, offset);
	    
	    if (ratings.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }
	    return ResponseEntity.ok(ratings);
	    
	}
	
	@GetMapping("/{ratingId}/replies")
	public ResponseEntity<List<ReviewReply>> getReviewReplies(
	        @PathVariable @NotBlank String ratingId,
	        @RequestParam(defaultValue = "0") @Min(value = 0) int offset) {

		List<ReviewReply> replies = reviewReplyService.getReviewReplies(ratingId, offset);
		
	    return ResponseEntity.ok(replies);
	}
	
	@GetMapping("/{stylistId}/ratings-summary")
	public ResponseEntity<RatingsSummary> getRatingsSummary(
			@PathVariable @NotBlank String stylistId) {

		return ResponseEntity.ok(customRatingRepository.getRatingsSummary(stylistId));
	}
	
	@PatchMapping("/{ratingId}/helpful")
    public ResponseEntity<Void> incrementRelevance(@PathVariable @NotBlank String ratingId) {
        
        if (customRatingRepository.incrementRelevance(ratingId)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
	
	@PostMapping
	public Mono<?> postReview(
			@RequestBody @Valid RatingDTO ratingDto) {

		return bookingService.isBookingCustomer(ratingDto.getCustomer().getId(),
				ratingDto.getBookingId()).flatMap(positive -> {
			if (positive) {
				reviewService.addRating(RatingMapper.toRating(ratingDto));
				return Mono.empty();
			} else {
				return Mono.error(new Exception());
			}
		});
	}
	
	@DeleteMapping("/{ratingId}")
	public ResponseEntity<?> removeRating(
			@PathVariable @NotBlank String ratingId,
			@RequestParam @NotBlank String customerId) {
		
		reviewService.deleteRating(ratingId, customerId);
		
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/{ratingId}/review/replies")
	public ResponseEntity<?> addReviewReply(
			@PathVariable @NotBlank String ratingId,
			@RequestBody @Valid ReplyDTO replyDto) {
		
		reviewService.addReplyToReview(ratingId, RatingMapper.toReply(replyDto));
		
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/{ratingId}/replies/{replyId}")
	public ResponseEntity<Void> deleteReviewReply(
	        @PathVariable @NotBlank String ratingId,
	        @PathVariable @NotBlank String replyId) {

		reviewService.deleteReplyFromReview(ratingId, replyId);

		return ResponseEntity.noContent().build();
	}
}
