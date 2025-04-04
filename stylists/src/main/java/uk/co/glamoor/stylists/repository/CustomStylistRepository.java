package uk.co.glamoor.stylists.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import uk.co.glamoor.stylists.enums.OrderOption;
import uk.co.glamoor.stylists.model.Location;
import uk.co.glamoor.stylists.model.ServiceProvider;
import uk.co.glamoor.stylists.model.Stylist;
import uk.co.glamoor.stylists.model.StylistRequestType;
import uk.co.glamoor.stylists.model.StylistServiceSpecification;
import uk.co.glamoor.stylists.model.Availability.Status;
import uk.co.glamoor.stylists.model.GlamoorService;
import uk.co.glamoor.stylists.repository.AggregationHelper.ProjectMode;

@Service
public class CustomStylistRepository {

	private final MongoTemplate mongoTemplate;
	private final String collection = "stylists";
	private final String favouritesCollection = "stylist-favourites";
	private final AggregationHelper aggregationHelper;
	
	public CustomStylistRepository(MongoTemplate mongoTemplate, AggregationHelper aggregationHelper) {

		this.mongoTemplate = mongoTemplate;

        this.aggregationHelper = aggregationHelper;
    }

	public List<Stylist> getSuggestedStylists(Location location, String customerId,
									 long offset, int batchSize, double maxDistance,
											  List<String> preferredServices) {

		AggregationOperation lookupFavouritesStage = context -> aggregationHelper
				.buildFavouritesLookup(customerId, favouritesCollection);

		GeoNearOperation geoNearOperation = location != null && location.getCoordinates() != null
				&& location.getCoordinates().get(0) != null && location.getCoordinates().get(1) != null ?
				aggregationHelper.buildLocationOperation(location.getCoordinates().get(0),
						location.getCoordinates().get(1), maxDistance) : null;

		Aggregation aggregation = Aggregation.newAggregation(

				geoNearOperation,
				lookupFavouritesStage,
				aggregationHelper.addFavouriteField(),
				aggregationHelper.buildProjectionOperation(ProjectMode.SHORT),
//        		Aggregation.match(Criteria.where("status").is("ACTIVE")),
//				aggregationHelper.sortAggregation(requestType),
				Aggregation.skip(offset),
				Aggregation.limit(batchSize)
		);

		AggregationResults<Stylist> results = mongoTemplate.aggregate(aggregation, collection, Stylist.class);

		return results.getMappedResults();
	}
	
	public List<Stylist> getStylists(Location location, String customerId, 
			long offset, int batchSize, double maxDistance, StylistRequestType requestType) {
		
	    AggregationOperation lookupFavouritesStage = context -> aggregationHelper
	    		.buildFavouritesLookup(customerId, favouritesCollection);

		GeoNearOperation geoNearOperation = location != null && location.getCoordinates() != null
				&& location.getCoordinates().get(0) != null && location.getCoordinates().get(1) != null ?
				aggregationHelper.buildLocationOperation(location.getCoordinates().get(0),
						location.getCoordinates().get(1), maxDistance) : null;
		
        Aggregation aggregation = Aggregation.newAggregation(

				geoNearOperation,
        		lookupFavouritesStage,
				aggregationHelper.addFavouriteField(),
				aggregationHelper.buildProjectionOperation(ProjectMode.SHORT),
//        		Aggregation.match(Criteria.where("status").is("ACTIVE")),
				aggregationHelper.sortAggregation(requestType),
        		Aggregation.skip(offset),
        		Aggregation.limit(batchSize)
        );

        AggregationResults<Stylist> results = mongoTemplate.aggregate(aggregation, collection, Stylist.class);

        return results.getMappedResults();
	}

	public List<Stylist> getRecentlyViewedStylists(String customer, int batchSize) {

		AggregationOperation lookupFavouritesStage = context -> aggregationHelper
				.buildFavouritesLookup(customer, favouritesCollection);

		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("customer").is(customer)),

				Aggregation.addFields().addFieldWithValue(
						"stylistObjectId", ConvertOperators.ToObjectId.toObjectId("$stylist")
				).build(),

				Aggregation.lookup("stylists", "stylistObjectId", "_id", "stylistDetails"),

				Aggregation.unwind("stylistDetails"),

				Aggregation.replaceRoot("stylistDetails"),
				lookupFavouritesStage,
				aggregationHelper.addFavouriteField(),
				aggregationHelper.buildProjectionOperation(ProjectMode.SHORT),
				Aggregation.sort(Sort.Direction.DESC, "time"),
				Aggregation.limit(batchSize)
		);

		// Execute the aggregation pipeline
		AggregationResults<Stylist> results = mongoTemplate.aggregate(
				aggregation,
				"recently-viewed-stylists",
				Stylist.class
		);

		return results.getMappedResults();
	}

	public List<Stylist> getFavouriteStylistsForCustomer(String customerId, OrderOption order, int offset,
														 int batchSize, String searchString) {

		Criteria matchCustomer = Criteria.where("customer").is(customerId);

		List<Criteria> searchCriteria = new ArrayList<>();
		if (searchString != null && !searchString.isEmpty()) {
			searchCriteria.add(Criteria.where("firstName").regex(searchString, "i"));
			searchCriteria.add(Criteria.where("lastName").regex(searchString, "i"));
			searchCriteria.add(Criteria.where("brand").regex(searchString, "i"));
			searchCriteria.add(Criteria.where("alias").regex(searchString, "i"));
		}

		Criteria stylistFilter = new Criteria().orOperator(searchCriteria.toArray(new Criteria[0]));

		Sort sort = switch (order) {
            case EARLIEST -> Sort.by(Sort.Direction.ASC, "timeCreated");
            case LATEST -> Sort.by(Sort.Direction.DESC, "timeCreated");
            default -> Sort.by(Sort.Direction.DESC, "rating");
        };

		List<AggregationOperation> operations = new ArrayList<>();

		operations.add(Aggregation.match(matchCustomer));

		operations.add(Aggregation.addFields().addFieldWithValue(
				"stylistObjectId", ConvertOperators.ToObjectId.toObjectId("$stylist")
		).build());

		operations.add(Aggregation.lookup("stylists", "stylistObjectId", "_id", "stylistDetails"));

		operations.add(Aggregation.unwind("stylistDetails"));

		operations.add(Aggregation.addFields().addFieldWithValue(
				"stylistDetails.timeCreated", "$timeCreated").build());

//		operations.add(Aggregation.project("stylistDetails.timeCreated", "stylistDetails"));

		operations.add(Aggregation.replaceRoot("stylistDetails"));

		if (searchString != null && !searchString.isEmpty()) {
			operations.add(Aggregation.match(stylistFilter));
		}

		operations.add(aggregationHelper.buildProjectionOperation(ProjectMode.SHORT));
		operations.add(Aggregation.sort(sort));
		operations.add(Aggregation.skip(offset));
		operations.add(Aggregation.limit(batchSize));

		Aggregation aggregation = Aggregation.newAggregation(operations);

		final List<Stylist> results = mongoTemplate.aggregate(aggregation, "stylist-favourites", Stylist.class)
				.getMappedResults();
		results.forEach(stylist -> stylist.setFavourite(true));

		return results;
	}


	public List<Stylist> findStylists(List<String> stylistIds) {
		Query query = new Query(Criteria.where("_id").in(stylistIds));
		return mongoTemplate.find(query, Stylist.class, "stylists");
	}

	public Optional<Stylist> findStylistById(String id, String customerId) {

		AggregationOperation lookupFavouritesStage = context -> aggregationHelper
				.buildFavouritesLookup(customerId, favouritesCollection);

		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("_id").is(new ObjectId(id))),
				Aggregation.unwind("serviceSpecifications"),
				Aggregation.sort(Sort.by(Sort.Direction.ASC, "serviceSpecifications.service.name")),
				Aggregation.group("_id")
						.first("brand").as("brand")
						.first("firstName").as("firstName")
						.first("lastName").as("lastName")
						.first("logo").as("logo")
						.first("email").as("email")
						.first("uid").as("uid")
						.first("accountProvider").as("accountProvider")
						.first("emailVerified").as("emailVerified")
						.first("alias").as("alias")
						.first("locality").as("locality")
						.first("business").as("business")
						.first("phone").as("phone")
						.first("phoneVerified").as("phoneVerified")
						.first("verified").as("verified")
						.first("rating").as("rating")
						.first("ratings").as("ratings")
						.first("terms").as("terms")
						.first("status").as("status")
						.first("about").as("about")
						.first("vat").as("vat")
						.first("currency").as("currency")
						.first("homeServiceSpecifications").as("homeServiceSpecifications")
						.first("address").as("address")
						.first("location").as("location")
						.first("banner").as("banner")
						.first("bookingCancellationTimeLimitMinutes").as("bookingCancellationTimeLimitMinutes")
						.first("bookingTimeLimitMinutes").as("bookingTimeLimitMinutes")
						.first("serviceCategories").as("serviceCategories")
						.first("timeCreated").as("timeCreated")
						.first("timeUpdated").as("timeUpdated")
						.first("favourite").as("favourite")
						.first("serviceProviders").as("serviceProviders")
						.push("serviceSpecifications").as("serviceSpecifications"),
				lookupFavouritesStage,
				aggregationHelper.addFavouriteField(),
				aggregationHelper.buildProjectionOperation(ProjectMode.LONG)
		);

		List<Stylist> results = mongoTemplate.aggregate(aggregation, collection, Stylist.class).getMappedResults();

		return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
	}
	
	public List<Stylist> search(String searchText, int offset, 
			String customerId, int batchSize) {
		
		AggregationOperation lookupFavouritesStage = context -> aggregationHelper
	    		.buildFavouritesLookup(customerId, favouritesCollection);
		
		
        Aggregation aggregation = Aggregation.newAggregation(
				aggregationHelper.searchMatch(searchText),
        		Aggregation.match(Criteria.where("status").is("ACTIVE")),
        		Aggregation.skip(offset),
        		Aggregation.limit(batchSize),
	            lookupFavouritesStage,
				aggregationHelper.buildProjectionOperation(ProjectMode.SHORT)
        );

        AggregationResults<Stylist> results = mongoTemplate.aggregate(aggregation, collection, Stylist.class);
        return results.getMappedResults();
	}
	
//	mongoTemplate.indexOps(Stylist.class).ensureIndex(new GeospatialIndex("location"));
//    mongoTemplate.indexOps(Stylist.class).ensureIndex(new Index().on("serviceSpecifications.service.id", Sort.Direction.ASC));
//    mongoTemplate.indexOps(Stylist.class).ensureIndex(new Index().on("availabilities.time", Sort.Direction.ASC)
//    		.on("availabilities.availableTimeMinutes", Sort.Direction.ASC));
    
	
	public List<Stylist> find(LocalDateTime fromTime, LocalDateTime toTime, 
			Double maxPrice, Double maxRadius, Boolean homeServiceAvailable,
			String customerId, String lastId, int batchSize, Location location,
			String serviceId, String cityId) {		
		
		List<AggregationOperation> pipeline = new ArrayList<>();
		
		pipeline.add(Aggregation.match(Criteria.where("status").is("ACTIVE")));

        if (serviceId != null) {
        	
            pipeline.add(Aggregation.match(Criteria.where("serviceSpecifications.service.id").is(serviceId)));

            addAvailabilityFilter(pipeline, fromTime, toTime, "availabilities", "serviceSpecifications.duration");

            if (Boolean.TRUE.equals(homeServiceAvailable)) {
                pipeline.add(Aggregation.match(
                        Criteria.where("homeServiceCities").ne(null)
                                .and("serviceSpecifications.homeServiceAvailable").is(true)
                                .and("serviceProviders.doesHomeService").is(true)
                                .and("serviceProviders.services").in(serviceId)
                ));
            } else {
                pipeline.add(Aggregation.match(
                        Criteria.where("serviceProviders.services").in(serviceId)
                ));
            }
        }
        
        if (Boolean.TRUE.equals(homeServiceAvailable) && serviceId == null) {
        	pipeline.add(Aggregation.match(
                    Criteria.where("homeServiceCities").ne(null)));
        }
        
        if (maxPrice != null) {
        	pipeline.add(Aggregation.match(Criteria.where("serviceSpecifications.price").lte(maxPrice)));
        }
        
        if ((fromTime != null || toTime != null) && serviceId == null) {
        	addAvailabilityFilter(pipeline, fromTime, toTime, "availabilities", "serviceSpecifications.duration");

        }
        
        if (cityId != null) {
            pipeline.add(Aggregation.match(Criteria.where("city.id").is(cityId)));
        } else if (maxRadius != null && location != null) {
            pipeline.add(Aggregation.match(Criteria.where("location.coordinates").nearSphere(
            		new Point(location.getCoordinates().get(0), location.getCoordinates().get(1)))
            		.maxDistance(maxRadius)
            ));
        }
        
        pipeline.add(Aggregation.addFields().addFieldWithValue(
        	    "serviceSpecifications",
        	    new Document("$slice", Arrays.asList(
        	        new Document("$let", new Document("vars", new Document("sampled",
        	            new Document("$arrayElemAt", Arrays.asList(
        	                new Document("$sample", new Document("input", "$serviceSpecifications").append("size", 5)),
        	                0
        	            ))
        	        )).append("in", "$$sampled")),
        	        5
        	    ))
        	).build());
        
        pipeline.add(context -> aggregationHelper.buildFavouritesLookup(customerId, favouritesCollection));
        pipeline.add(aggregationHelper.buildProjectionOperation(ProjectMode.SHORT));

        if (lastId != null && ObjectId.isValid(lastId)) {
            pipeline.add(Aggregation.match(Criteria.where("_id").gt(new ObjectId(lastId))));
        }

        pipeline.add(Aggregation.sort(Sort.Direction.ASC, "_id"));
        
        pipeline.add(Aggregation.limit(batchSize));
		
        Aggregation aggregation = Aggregation.newAggregation(pipeline);
        
        return mongoTemplate.aggregate(aggregation, collection, Stylist.class)
        		.getMappedResults();
		
	}
	
	private void addAvailabilityFilter(
			List<AggregationOperation> pipeline, LocalDateTime fromTime,
					LocalDateTime toTime, String lookupCollection, String durationField) {

	    if (fromTime != null || toTime != null) {
	        List<Criteria> availabilityCriteria = new ArrayList<>();
	        if (fromTime != null) {
	            availabilityCriteria.add(Criteria.where("time").gte(fromTime));
	        }
	        if (toTime != null) {
	            availabilityCriteria.add(Criteria.where("time").lte(toTime));
	        }
	        availabilityCriteria.add(Criteria.where("availableTimeMinutes").gte(durationField).and("status").is(Status.AVAILABLE));

	        pipeline.add(Aggregation.lookup(lookupCollection, "_id", "stylistId", "matchedAvailabilities"));
	        pipeline.add(Aggregation.match(Criteria.where("matchedAvailabilities").elemMatch(new Criteria().andOperator(availabilityCriteria))));
	        
	    }
	}

	public List<StylistServiceSpecification> getServiceSpecifications(String stylistId, int offset, int batchSize,
																	  String serviceCategory) {
		List<AggregationOperation> stages = new ArrayList<>();

// Match stylist by ID
		stages.add(Aggregation.match(Criteria.where("_id").is(new ObjectId(stylistId))));

// Conditionally filter serviceSpecifications if serviceCategory is not null
		if (serviceCategory != null && !serviceCategory.equalsIgnoreCase("null")) {
			stages.add(Aggregation.project()
					.andInclude("_id")
					.and("_id").as("id")
					.and(ArrayOperators.Filter.filter("serviceSpecifications")
							.as("spec")
							.by(ComparisonOperators.Eq.valueOf("spec.service.categoryId").equalToValue(serviceCategory)))
					.as("serviceSpecifications"));
		} else {
			// Include all serviceSpecifications if no filtering is needed
			stages.add(Aggregation.project()
					.andInclude("_id")
					.and("_id").as("id")
					.and("serviceSpecifications").as("serviceSpecifications"));
		}

// Unwind serviceSpecifications to sort its elements
		stages.add(Aggregation.unwind("serviceSpecifications"));

// Sort by service name
		stages.add(Aggregation.sort(Sort.by(Sort.Direction.ASC, "serviceSpecifications.service.name")));

// Group back into an array
		stages.add(Aggregation.group("_id")
				.first("_id").as("id")
				.push("serviceSpecifications").as("serviceSpecifications"));

// Slice the array for pagination
		stages.add(Aggregation.project()
				.andInclude("_id")
				.and("_id").as("id")
				.and(ArrayOperators.Slice.sliceArrayOf("serviceSpecifications")
						.offset(offset).itemCount(batchSize))
				.as("serviceSpecifications"));

// Build the aggregation
		Aggregation aggregation = Aggregation.newAggregation(stages);

		AggregationResults<Stylist> results = mongoTemplate.aggregate(
				aggregation,
				"stylists",
				Stylist.class
		);

		if (results.getMappedResults().isEmpty()) return new ArrayList<>();

		return results.getMappedResults().get(0).getServiceSpecifications();

	}

	public List<ServiceProvider> getServiceProvidersForServices(String stylistId) {
	    
	    List<AggregationOperation> pipeline = new ArrayList<>();

	    pipeline.add(Aggregation.match(Criteria.where("_id").is(new ObjectId(stylistId))));

	    pipeline.add(Aggregation.project().andInclude("serviceProviders"));
	    Aggregation aggregation = Aggregation.newAggregation(pipeline);
	    AggregationResults<Stylist> result = mongoTemplate.aggregate(aggregation, collection, Stylist.class);

	    if (result.getMappedResults().isEmpty()) {
	        return Collections.emptyList();
	    }

	    return result.getMappedResults().get(0).getServiceProviders();
	}
	
	public void updateService(GlamoorService service) {
	    Query query = new Query(Criteria.where("serviceSpecifications.service.id").is(service.getId()));

	    Update update = new Update().set("serviceSpecifications.$.service", service);

	    mongoTemplate.updateMulti(query, update, Stylist.class);
	}
	
}
