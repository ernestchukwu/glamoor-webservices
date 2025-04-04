package uk.co.glamoor.posts.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GeoNearOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import uk.co.glamoor.posts.dto.PostDTO;
import uk.co.glamoor.posts.model.Location;
import uk.co.glamoor.posts.model.Post;
import uk.co.glamoor.posts.model.PostSave;

@Service
public class CustomPostRepository {
	
	private final MongoTemplate mongoTemplate;

    public CustomPostRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

	public List<Post> getLatestPosts(Location location,
										String customerId, long offset, int batchSize, double maxDistance) {
		
	    AggregationOperation lookupLikesStage = context -> AggregationBuilder.getLikesLookup(customerId);
	    AggregationOperation lookupSavesStage = context -> AggregationBuilder.getSavesLookup(customerId);

		AggregationOperation addLikedField = context -> AggregationBuilder
				.addLikedField();
		AggregationOperation addSavedField = context -> AggregationBuilder
				.addSavedField();

		GeoNearOperation geoNearOperation = location != null && location.getCoordinates() != null
				&& location.getCoordinates().get(0) != null && location.getCoordinates().get(1) != null ?
				AggregationBuilder.buildLocationOperation(location.getCoordinates().get(0),
						location.getCoordinates().get(1), maxDistance) : null;
		
        Aggregation aggregation = Aggregation.newAggregation(

				geoNearOperation,
				lookupLikesStage,
				lookupSavesStage,
				addLikedField,
				addSavedField,
				AggregationBuilder.getProjectionOp(),
        		AggregationBuilder.sortAggregation,
        		Aggregation.skip(offset),
        		Aggregation.limit(batchSize)
        );

        AggregationResults<Post> results = mongoTemplate.aggregate(aggregation, "posts", Post.class);
        return results.getMappedResults();
	}
	
	public List<PostDTO> getPosts(String stylistId,
								  String customerId, long offset, int batchSize) {
		
	    AggregationOperation lookupLikesStage = context -> AggregationBuilder.getLikesLookup(customerId);
	    AggregationOperation lookupSavesStage = context -> AggregationBuilder.getSavesLookup(customerId);
		
		
        Aggregation aggregation = Aggregation.newAggregation(
        		AggregationBuilder.getMatchStylistStage(stylistId),
        		AggregationBuilder.sortAggregation,
        		Aggregation.skip(offset),
        		Aggregation.limit(batchSize),
	            lookupLikesStage,
	            lookupSavesStage,
	            AggregationBuilder.getAddedFields(),
	            AggregationBuilder.getProjectionOp()
        );

        AggregationResults<PostDTO> results = mongoTemplate.aggregate(aggregation, "posts", PostDTO.class);
        return results.getMappedResults();
	}
	
	public List<PostDTO> getSavedPostsByCustomer(
			String customerId, long offset, int batchSize) {
		
		AggregationOperation lookupLikesStage = context -> AggregationBuilder.getLikesLookup(customerId);
	    AggregationOperation lookupSavesStage = context -> AggregationBuilder.getSavesLookup(customerId);
		
        Aggregation aggregation = Aggregation.newAggregation(
        		Aggregation.match(Criteria.where("_id").in(
        		        mongoTemplate.find(Query.query(Criteria.where("customer").is(customerId)), PostSave.class)
        		            .stream()
        		            .map(PostSave::getPost)
        		            .collect(Collectors.toList())
        		    )),
        		AggregationBuilder.sortAggregation,
        		Aggregation.skip(offset),
        		Aggregation.limit(batchSize),
	            lookupLikesStage,
	            lookupSavesStage,
	            AggregationBuilder.getAddedFields(),
	            AggregationBuilder.getProjectionOp()
        );

        AggregationResults<PostDTO> results = mongoTemplate.aggregate(aggregation, "posts", PostDTO.class);
        return results.getMappedResults();
	}
	
	public void updateStylistAlias(String stylistId, String alias) {
        Query query = new Query(Criteria.where("stylist.id").is(stylistId));
        Update update = new Update().set("stylist.alias", alias);

        mongoTemplate.updateMulti(query, update, Post.class);
    }
}
