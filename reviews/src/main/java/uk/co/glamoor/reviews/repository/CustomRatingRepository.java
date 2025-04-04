package uk.co.glamoor.reviews.repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.result.UpdateResult;

import uk.co.glamoor.reviews.model.Rating;
import uk.co.glamoor.reviews.model.RatingsSummary;
import uk.co.glamoor.reviews.model.RequestOrder;
import uk.co.glamoor.reviews.model.ReviewReply;

@Service
public class CustomRatingRepository {

	private final MongoTemplate mongoTemplate;
	
	public CustomRatingRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public List<Rating> getRatings(String stylistId, Integer rating,
								   RequestOrder requestOrder, int offset, int batchSize,
								   int replyBatchSize) {

		Criteria criteria = Criteria.where("stylistId").is(stylistId);

		if (rating != null) {
			criteria.and("rating").is(rating);
		}

		Sort sort;
		switch (requestOrder) {
			case BEST:
				sort = Sort.by(Sort.Direction.DESC, "rating");
				break;
			case WORST:
				sort = Sort.by(Sort.Direction.ASC, "rating");
				break;
			case LATEST:
				sort = Sort.by(Sort.Direction.DESC, "time");
				break;
			case EARLIEST:
				sort = Sort.by(Sort.Direction.ASC, "time");
				break;
			default:
				sort = Sort.by(Sort.Direction.DESC, "review.relevance");
				break;
		}

		AggregationOperation lookupReplies = context -> new Document("$lookup", new Document()
				.append("from", "review-replies")
				.append("let", new Document("ratingId", "$_id")) // Pass the local rating ID to the pipeline
				.append("pipeline", Arrays.asList(
						new Document("$match", new Document("$expr", new Document("$eq", Arrays.asList("$ratingId", "$$ratingId")))), // Match replies with the rating ID
						new Document("$sort", new Document("time", -1)), // Sort replies by time descending
						new Document("$limit", replyBatchSize) // Limit the number of replies
				))
				.append("as", "replies"));

		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(criteria),
				Aggregation.sort(sort),
				Aggregation.skip(offset),
				Aggregation.limit(batchSize),
				lookupReplies
		);

		AggregationResults<Rating> results = mongoTemplate.aggregate(
				aggregation,
				"reviews", // Collection name for Rating
				Rating.class // DTO class
		);

		return results.getMappedResults();
	}

	

	
	public RatingsSummary getRatingsSummary(String stylistId) {
		
		Aggregation aggregation = Aggregation.newAggregation(
	            Aggregation.match(Criteria.where("stylistId").is(stylistId)),
	            Aggregation.group("rating").count().as("count")
	    );

	    AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "reviews", Document.class);

	    RatingsSummary summary = new RatingsSummary();

	    results.getMappedResults().forEach(document -> {
	        int rating = document.getInteger("_id");
	        int count = document.getInteger("count");

	        switch (rating) {
	            case 1 -> summary.setRating1(count);
	            case 2 -> summary.setRating2(count);
	            case 3 -> summary.setRating3(count);
	            case 4 -> summary.setRating4(count);
	            case 5 -> summary.setRating5(count);
	        }
	    });
	    
	    return summary;
	}
	
	public boolean incrementRelevance(String ratingId) {
		UpdateResult result = mongoTemplate.updateFirst(
	            Query.query(Criteria.where("_id").is(ratingId)),
	            new Update().inc("review.relevance", 1),
	            Rating.class
	        );

	    return result.getMatchedCount() > 0;
	}
	
	public void addReplyToReview(String ratingId, ReviewReply reply) {
		
		Query query = new Query(Criteria.where("_id")
				.is(ratingId)
				.and("review")
				.exists(true));

        Update update = new Update().push("review.replies", reply);

        mongoTemplate.updateFirst(query, update, Rating.class);

	}
	
	public void deleteReplyFromReview(String ratingId, String replyId) {
	    
		Query query = new Query(Criteria.where("_id").is(ratingId).and("review.replies.id").is(replyId));
	    Update update = new Update().pull("review.replies", new Document("id", replyId));
	    mongoTemplate.updateFirst(query, update, Rating.class);

	}
	
	public void anonymiseCustomer(String customerId) {

		Query query = new Query(Criteria.where("customer.id").is(customerId));

	    Update update = new Update()
	            .set("customer.firstName", "Anonymous")
	            .set("customer.lastName", "User")
	            .set("customer.profilePicture", null);

	    mongoTemplate.updateMulti(query, update, Rating.class);

	}

}
