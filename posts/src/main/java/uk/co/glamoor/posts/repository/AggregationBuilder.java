package uk.co.glamoor.posts.repository;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.GeoNearOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;

public class AggregationBuilder {
	
	public static SortOperation sortAggregation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "time"));
	
	public static MatchOperation getMatchStylistStage(String stylistId) {
		return Aggregation.match(Criteria.where("stylist._id").is(new ObjectId(stylistId)));
	}
	
	public static ProjectionOperation getProjectionOp() {
		return Aggregation.project()
                .andInclude("_id", "stylist", "location", "description",
						"photos", "time", "likes", "status", "liked", "saved", "services")
				.and("_id").as("id");
	}
	
	
	public static AddFieldsOperation getAddedFields() {
		return Aggregation.addFields()
        		.addField("liked").withValueOf(AggregationExpression.from(
        				MongoExpression.create("$gt: [ { $size: '$customerLikes' }, 0 ]")))
        		.addField("saved").withValueOf(AggregationExpression.from(
        				MongoExpression.create("$gt: [ { $size: '$saves' }, 0 ]")))
        		.build();
	}
	public static GeoNearOperation buildLocationOperation(double longitude,
														  double latitude, double maxDistance) {
		return new GeoNearOperation(
				NearQuery.near(new Point(
								longitude, latitude))
						.spherical(true)
						.maxDistance(maxDistance), "distance"
		);
	}
	
	public static Document getLikesLookup(String customerId) {
		return new Document("$lookup", new Document()
		        .append("from", "post-likes")
		        .append("let", new Document("post", new Document("$toString", "$_id")))
				.append("pipeline", List.of(
						new Document("$match", new Document("$expr", new Document("$and", Arrays.asList(
								new Document("$eq", Arrays.asList("$post", "$$post")),
								new Document("$eq", Arrays.asList("$customer", customerId))
						))))
		        ))
		        .append("as", "customerLikes")
		    );
	}

	public static Document addLikedField() {
		return new Document("$addFields", new Document("liked",
				new Document("$gt", Arrays.asList(new Document("$size", "$customerLikes"), 0))
		));
	}

	public static Document addSavedField() {
		return new Document("$addFields", new Document("saved",
				new Document("$gt", Arrays.asList(new Document("$size", "$saves"), 0))
		));
	}
	
	public static Document getSavesLookup(String customerId) {
		return new Document("$lookup", new Document()
				.append("from", "post-saves")
				.append("let", new Document("post", new Document("$toString", "$_id")))
				.append("pipeline", List.of(
						new Document("$match", new Document("$expr", new Document("$and", Arrays.asList(
								new Document("$eq", Arrays.asList("$post", "$$post")),
								new Document("$eq", Arrays.asList("$customer", customerId))
						))))
				))
				.append("as", "saves")
		);
	}
}
