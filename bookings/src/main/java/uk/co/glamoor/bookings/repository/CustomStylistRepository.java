package uk.co.glamoor.bookings.repository;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import uk.co.glamoor.bookings.model.Stylist;
import uk.co.glamoor.bookings.model.GlamoorService;

@Service
public class CustomStylistRepository {
	
    private final MongoTemplate mongoTemplate;
    
    public CustomStylistRepository(MongoTemplate mongoTemplate) {
    	this.mongoTemplate = mongoTemplate;
    }
	
	public Stylist getFilteredStylist(String stylistId, String serviceProviderId, List<String> serviceSpecificationId) {
        
		
		AggregationOperation projectFilteredFields = context -> new Document("$project", new Document()
			    .append("id", 1)
			    .append("name", 1)
			    .append("logo", 1)
			    .append("email", 1)
			    .append("address", 1)
			    .append("phone", 1)
			    .append("vat", 1)
			    .append("bookingCancellationTimeLimitMinutes", 1)
			    .append("homeServiceSpecifications", 1)
			    .append("serviceProviders", new Document("$filter", new Document()
			        .append("input", "$serviceProviders")
			        .append("as", "provider")
			        .append("cond", new Document("$eq", Arrays.asList("$$provider.id", serviceProviderId)))))
			    .append("stylistServiceSpecifications", new Document("$filter", new Document()
			        .append("input", "$stylistServiceSpecifications")
			        .append("as", "spec")
			        .append("cond", new Document("$in", Arrays.asList("$$spec.id", serviceSpecificationId))))
			    )
			);
		
		Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("_id").is(stylistId)),
            projectFilteredFields
        );

        AggregationResults<Stylist> results = mongoTemplate.aggregate(aggregation, "stylists", Stylist.class);

        return results.getUniqueMappedResult();
    }
	
	public void updateService(GlamoorService service) {
	    Query query = new Query(Criteria.where("serviceSpecifications.service.id").is(service.getId()));

	    Update update = new Update().set("serviceSpecifications.$.service", service);

	    mongoTemplate.updateMulti(query, update, Stylist.class);
	}
	
}
