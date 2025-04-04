package uk.co.glamoor.bookings.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import uk.co.glamoor.bookings.dto.request.MessageRequest;

@Service
public class CustomBookingRepository {
	
	private final MongoTemplate mongoTemplate;
	
	public CustomBookingRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	public List<MessageRequest> findMessagesByBookingIdAndCustomerId(String customerId, String bookingId, int offset,
																	 int limit) {
		
		Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(bookingId).and("customer._id").is(customerId)),
//                Aggregation.project("messages"),
                Aggregation.unwind("messages"),
                Aggregation.project()
                .and("messages.id").as("id")
                .and("messages.sender").as("sender")
                .and("messages.message").as("message")
                .and("messages.time").as("time")
                .and("messages.containsImage").as("containsImage")
                .and("messages.seen").as("seen"),
                Aggregation.sort(Sort.by(Sort.Order.desc("messages.time"))),
                Aggregation.skip(offset),
                Aggregation.limit(limit)
        );

        AggregationResults<MessageRequest> results = mongoTemplate.aggregate(aggregation, "bookings", MessageRequest.class);
        return results.getMappedResults();
	}


}
