package uk.co.glamoor.stylists.repository;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.BsonRegularExpression;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.stereotype.Service;
import uk.co.glamoor.stylists.config.AppConfig;
import uk.co.glamoor.stylists.config.StylistsConfig;
import uk.co.glamoor.stylists.model.StylistRequestType;

@Service
public class AggregationHelper {

	private final AppConfig appConfig;
	private final StylistsConfig stylistsConfig;

    public AggregationHelper(AppConfig appConfig, StylistsConfig stylistsConfig) {
        this.appConfig = appConfig;
        this.stylistsConfig = stylistsConfig;
    }

    public enum ProjectMode {
		SHORT, MEDIUM, LONG
	}
	
	public enum AvailabilityType {
		FROM, TO, FROM_TO
	}
	
	public SortOperation sortAggregation(StylistRequestType requestType) {
		switch(requestType) {
			case LATEST:
				return Aggregation.sort(Sort.by(Sort.Direction.DESC, "timeCreated"));
			case SUGGESTED:
				return Aggregation.sort(Sort.by(Sort.Direction.DESC, "alias"));
			default:
				return Aggregation.sort(Sort.by(Sort.Direction.DESC, "alias"));
		}
		
	}
	
	
	public ProjectionOperation buildProjectionOperation(ProjectMode projectMode) {
		switch (projectMode) {
			case SHORT:
				return Aggregation.project().andInclude("brand", "firstName", "lastName", "logo",
								"alias", "locality", "business", "phone", "favourite", "serviceCategories",
								"rating", "ratings", "banner", "serviceSpecifications", "verified", "time",
								"currency", "location", "homeServiceSpecifications", "timeCreated").and("_id").as("id");
//						.and(AggregationSpELExpression.expressionOf("homeServiceSpecifications != null && "
//								+ "homeServiceSpecifications.length > 0"))
//				        .as("homeServiceAvailable");
//						.and(ArrayOperators.Slice.sliceArrayOf("serviceSpecifications")
//								.itemCount(5))
//						.as("serviceSpecifications");
			case MEDIUM:
				return Aggregation.project().andInclude("brand", "firstName", "lastName", "logo",
						"email", "alias", "locality", "business", "phone", "rating", "ratings", "vat", "favourite",
						"about", "homeServiceCities", "city", "address", "banner", "serviceSpecifications", "verified",
						"bookingCancellationTimeLimitMinutes", "currency", "location", "serviceCategories", "time",
								"bookingTimeLimitMinutes", "homeServiceSpecifications")
						.and("_id").as("id")
						.and(ArrayOperators.Slice.sliceArrayOf("serviceSpecifications")
								.itemCount(stylistsConfig.getServiceSpecificationRequestBatchSizeMini()))
						.as("serviceSpecifications");
			default:
				return Aggregation.project().andInclude("brand", "firstName", "lastName", "logo",
								"email", "alias", "locality", "business", "phone", "rating", "ratings", "vat", "favourite",
								"about", "homeServiceCities", "city", "address", "banner", "serviceSpecifications", "verified",
								"bookingCancellationTimeLimitMinutes", "currency", "location", "serviceCategories", "time",
								"bookingTimeLimitMinutes", "homeServiceSpecifications")
						.and("_id").as("id")
						.and(ArrayOperators.Slice.sliceArrayOf("serviceSpecifications")
								.itemCount(stylistsConfig.getServiceSpecificationRequestBatchSizeMini()))
						.as("serviceSpecifications");
		}
		
	}

	public GeoNearOperation buildLocationOperation(double longitude,
			double latitude, double maxDistance) {
		return new GeoNearOperation(
                NearQuery.near(new Point(
                		longitude, latitude))
                	.spherical(true)
                    .maxDistance(maxDistance), "distance"
            );
	}

	public AddFieldsOperation addFavouriteField() {
		return Aggregation.addFields().addFieldWithValue(
				"favourite", new Document("$gt", Arrays.asList(
						new Document("$size", "$favourites"), 0))).build();
	}

	public Document buildFavouritesLookup(String customerId, String collection) {
		return new Document("$lookup", new Document()
				.append("from", collection)
				.append("let", new Document("stylist", new Document("$toString", "$_id")))
				.append("pipeline", List.of(
						new Document("$match", new Document("$expr", new Document("$and", Arrays.asList(
								new Document("$eq", Arrays.asList("$stylist", "$$stylist")),
								new Document("$eq", Arrays.asList("$customer", customerId))
						))))
				))
				.append("as", "favourites")
		);
	}
	
	
	
	public MatchOperation searchMatch(String searchText) {
		BsonRegularExpression bsonRegex = new BsonRegularExpression(searchText, "i");

		Criteria orCriteria = new Criteria().orOperator(
		        Criteria.where("firstName").regex(bsonRegex),
		        Criteria.where("lastName").regex(bsonRegex),
		        Criteria.where("alias").regex(bsonRegex),
		        Criteria.where("brand").regex(bsonRegex)
		);

		return Aggregation.match(orCriteria);
	}
	
	
	
}
