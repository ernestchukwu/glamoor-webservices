package uk.co.glamoor.stylists.service;

import java.time.LocalDateTime;
import java.util.*;

import org.bson.types.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import uk.co.glamoor.stylists.config.AppConfig;
import uk.co.glamoor.stylists.config.StylistsConfig;
import uk.co.glamoor.stylists.enums.OrderOption;
import uk.co.glamoor.stylists.exception.EntityNotFoundException;
import uk.co.glamoor.stylists.exception.EntityType;
import uk.co.glamoor.stylists.exception.ResourceNotFoundException;
import uk.co.glamoor.stylists.mapper.GlamoorJsonMapper;
import uk.co.glamoor.stylists.mapper.StylistMapper;
import uk.co.glamoor.stylists.model.*;
import uk.co.glamoor.stylists.dto.response.bookings.BookingsServiceStylistResponse;
import uk.co.glamoor.stylists.repository.*;

@Service
public class StylistService {
	
	private final CustomStylistRepository customStylistRepository;
	private final RecentlyViewedStylistRepository recentlyViewedStylistRepository;
	private final StylistRepository stylistRepository;
	private final StylistFavouriteRepository stylistFavouriteRepository;
	private final CustomerPreferencesService customerPreferencesService;
	private final GlamoorServiceService glamoorServiceService;
	private final MessagingService messagingService;
	private final AppConfig appConfig;
	private final StylistsConfig stylistsConfig;
	
	public StylistService(CustomStylistRepository customStylistRepository, RecentlyViewedStylistRepository recentlyViewedStylistRepository,
                          StylistFavouriteRepository stylistFavouriteRepository,
                          StylistRepository stylistRepository,
                          CustomerPreferencesService customerPreferencesService, GlamoorServiceService glamoorServiceService,
                          @Lazy MessagingService messagingService,
                          AppConfig appConfig, StylistsConfig stylistsConfig) {
		
		this.customStylistRepository = customStylistRepository;
        this.recentlyViewedStylistRepository = recentlyViewedStylistRepository;
        this.stylistFavouriteRepository = stylistFavouriteRepository;
		this.stylistRepository = stylistRepository;
        this.customerPreferencesService = customerPreferencesService;
        this.glamoorServiceService = glamoorServiceService;
        this.messagingService = messagingService;
		this.appConfig = appConfig;
        this.stylistsConfig = stylistsConfig;
    }

	public List<Stylist> getSuggestedStylists(Location location, String customerId,
									 int offset, boolean homeView) {

		CustomerPreferences customerPreferences = customerPreferencesService
				.findCustomerPreferences(customerId)
				.orElse(new CustomerPreferences());

		List<String> preferredServiceIds = !customerPreferences.getServicePreferences().isEmpty() ?
				customerPreferences.getServicePreferences().stream()
				.map(CustomerPreferences.ServicePreference::getServiceId).toList() :
				glamoorServiceService.getServices(0).stream().map(GlamoorService::getId).toList();

		int batchSize = homeView ? stylistsConfig
				.getStylistsRequestBatchSizeForHomeView():
				stylistsConfig.getStylistsRequestBatchSize();

		return stylistRepository.findStylistsByPreferredServices(
				location.getCoordinates().get(0), location.getCoordinates().get(1),
				preferredServiceIds.stream().map(ObjectId::new).toList(), customerId,
				offset, batchSize, appConfig.getQueryResultsMaxDistance()
		);
	}

	public List<Stylist> getFeaturedStylists(Location location, String customerId,
											  int offset, boolean homeView) {

		int batchSize = homeView ? stylistsConfig
				.getStylistsRequestBatchSizeForHomeView():
				stylistsConfig.getStylistsRequestBatchSize();

		return stylistRepository.findFeaturedNearbyStylists(
				location.getCoordinates().get(0), location.getCoordinates().get(1),
				offset, batchSize, customerId, appConfig.getQueryResultsMaxDistance());
	}

	public List<Stylist> getFavouriteStylistsForCustomer(String customerId, OrderOption order, int offset,
														 String searchString) {

		return customStylistRepository.getFavouriteStylistsForCustomer(customerId, order, offset,
				stylistsConfig.getStylistsRequestBatchSize(), searchString);
	}


	public Stylist getStylistById(String stylistId, String customerId) {

		Optional<Stylist> stylist = customStylistRepository.findStylistById(stylistId, customerId);

		if (stylist.isEmpty()) throw new ResourceNotFoundException(
				"Stylist with ID " + stylistId + " not found");

		if (customerId != null) {
			RecentlyViewedStylist recentlyViewedStylist = recentlyViewedStylistRepository
					.findByStylistAndCustomer(stylistId, customerId);
			if (recentlyViewedStylist != null) {
				recentlyViewedStylist.setTime(LocalDateTime.now());
				recentlyViewedStylistRepository.save(recentlyViewedStylist);
			} else {
				recentlyViewedStylistRepository.save(new RecentlyViewedStylist(stylistId, customerId));
			}
		}

		return stylist.get();
	}

	public Stylist getStylistById(String stylistId) {

		return stylistRepository.findById(stylistId).orElseThrow(
				() -> new ResourceNotFoundException(
						"Stylist with ID " + stylistId + " not found"));

	}

	public List<Stylist> getRecentlyViewedStylists(String customerId) {

		return stylistRepository.findRecentlyViewedStylists(customerId,
				0, stylistsConfig.getStylistsRequestBatchSizeForHomeView());


	}
	
	public List<StylistServiceSpecification> getServiceSpecifications(String stylistId, int offset,
																	  String serviceCategory) {
		return customStylistRepository.getServiceSpecifications(stylistId, offset, stylistsConfig
				.getServiceSpecificationRequestBatchSize(), serviceCategory);
	}
	
	public List<ServiceProvider> getServiceProvidersForServices(String stylistId){
		return customStylistRepository.getServiceProvidersForServices(stylistId);
	}

	public void likeStylist(String customerId, String stylistId) {
		Stylist stylist = stylistRepository.findById(stylistId)
				.orElseThrow(() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));

		Optional<StylistFavourite> existingLike = stylistFavouriteRepository.findByCustomerAndStylist(customerId, stylistId);
        if (existingLike.isEmpty()) {
        	StylistFavourite stylistFavourite = new StylistFavourite();
        	stylistFavourite.setCustomer(customerId);
        	stylistFavourite.setStylist(stylistId);
            stylistFavouriteRepository.save(stylistFavourite);

			customerPreferencesService.addPreferredService(customerId, stylist.getServiceSpecifications()
					.stream().map(spec -> spec.getService().getId()).toList());
        }
	}

	public void unlikeStylist(String customerId, String stylistId) {
		Stylist stylist = stylistRepository.findById(stylistId)
				.orElseThrow(() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));

		Optional<StylistFavourite> existingLike = stylistFavouriteRepository.findByCustomerAndStylist(customerId, stylistId);
        existingLike.ifPresent(stylistFavouriteRepository::delete);

		customerPreferencesService.removePreferredService(customerId, stylist.getServiceSpecifications()
				.stream().map(spec -> spec.getService().getId()).toList());
	}

	public List<Stylist> search(String searchText, int offset, String customerId) {

		return customStylistRepository.search(searchText, offset, customerId, stylistsConfig
				.getStylistsRequestBatchSize());
	}

	public Stylist createStylist(Stylist stylist) {
		Stylist savedStylist = stylistRepository.save(stylist);

		String strStylistMessage = GlamoorJsonMapper.toJson(StylistMapper.toBookingsServiceStylist(savedStylist));

		messagingService.sendMessage(MessagingService.BOOKING_EXCHANGE, MessagingService.BOOKINGS_STYLISTS_NEW_ROUTING_KEY, strStylistMessage);
		messagingService.sendMessage(MessagingService.NOTIFICATION_EXCHANGE, MessagingService.NOTIFICATIONS_USERS_NEW_ROUTING_KEY, strStylistMessage);

		return savedStylist;
	}

	public void updateStylist(Stylist update) {
		Stylist stylist = stylistRepository.findById(update.getId()).orElseThrow(
				() -> new EntityNotFoundException(update.getId(), EntityType.STYLIST));

		BookingsServiceStylistResponse bookingsServiceStylistResponse = StylistMapper.toBookingsServiceStylist(stylist);

		stylist = stylistRepository.save(StylistMapper.applyUpdates(stylist, update));

//		if (bookingsServiceStylistResponse.containsUpdate(stylist)) {
			String strStylistMessage = GlamoorJsonMapper.toJson(StylistMapper.toBookingsServiceStylist(stylist));

			messagingService.sendMessage(MessagingService.BOOKING_EXCHANGE, MessagingService.BOOKINGS_STYLISTS_UPDATE_ROUTING_KEY, strStylistMessage);
			messagingService.sendMessage(MessagingService.NOTIFICATION_EXCHANGE, MessagingService.NOTIFICATIONS_USERS_UPDATE_ROUTING_KEY, strStylistMessage);

			if (!bookingsServiceStylistResponse.getAlias().equals(stylist.getAlias())) {
				messagingService.sendMessage(MessagingService.POST_EXCHANGE,
						MessagingService.POSTS_STYLISTS_UPDATE_ROUTING_KEY,
						stylist.getId(), Map.of("alias", stylist.getAlias()));
			}
//		}
	}

	public void anonymiseStylist(String stylistId) {

		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));

		stylist = getAnonymousStylist(stylist.getId());

		stylistRepository.save(stylist);

		messagingService.sendMessage(MessagingService.BOOKING_EXCHANGE, MessagingService.BOOKINGS_STYLISTS_ANONYMISE_ROUTING_KEY, stylist.getId());
		messagingService.sendMessage(MessagingService.NOTIFICATION_EXCHANGE, MessagingService.NOTIFICATIONS_USERS_DELETE_ROUTING_KEY, stylist.getId());

		messagingService.sendMessage(MessagingService.POST_EXCHANGE, MessagingService.POSTS_STYLISTS_DELETE_ROUTING_KEY, stylist.getId());
		messagingService.sendMessage(MessagingService.REVIEW_EXCHANGE, MessagingService.REVIEWS_STYLISTS_DELETE_ROUTING_KEY, stylist.getId());

	}

	private Stylist getAnonymousStylist(String stylistId) {
		Stylist stylist = new Stylist();
		stylist.setId(stylistId);
		stylist.setFirstName("Anonymous");
		stylist.setLastName("User");
		stylist.setEmail("email_"+stylistId+"@mail.com");
		stylist.setAlias("alias_"+stylistId);
		return stylist;
	}

	public void updateService(GlamoorService service) {
		customStylistRepository.updateService(service);
	}

	public String addServiceProvider(String stylistId, ServiceProvider serviceProvider) {
		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));

		serviceProvider.setId(UUID.randomUUID().toString());

		stylist.getServiceProviders().add(serviceProvider);
		stylistRepository.save(stylist);

		messagingService.sendMessage(MessagingService.BOOKING_EXCHANGE,
				MessagingService.BOOKINGS_STYLISTS_SERVICE_PROVIDER_NEW_ROUTING_KEY,
				GlamoorJsonMapper.toJson(serviceProvider));

		return serviceProvider.getId();
	}

	public void updateServiceProvider(String stylistId, ServiceProvider serviceProvider) {
		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));

		List<ServiceProvider> serviceProviders = stylist.getServiceProviders();

		boolean found = false;

	    for (int i = 0; i < serviceProviders.size(); i++) {
	        if (serviceProviders.get(i).getId().equals(serviceProvider.getId())) {
	            serviceProviders.set(i, serviceProvider);
	            found = true;
	            break;
	        }
	    }

	    if (!found) {
	        throw new EntityNotFoundException(serviceProvider.getId(), EntityType.SERVICE_PROVIDER);
	    }

		stylistRepository.save(stylist);

		messagingService.sendMessage(MessagingService.BOOKING_EXCHANGE,
				MessagingService.BOOKINGS_STYLISTS_SERVICE_PROVIDER_UPDATE_ROUTING_KEY,
				GlamoorJsonMapper.toJson(serviceProvider), Map.of("stylistId", stylistId));
	}

	public void removeServiceProvider(String stylistId, String serviceProviderId) {
		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));

		boolean removed = stylist.getServiceProviders().removeIf(provider -> provider.getId().equals(serviceProviderId));
		if (!removed) {
		    throw new EntityNotFoundException(serviceProviderId, EntityType.SERVICE_PROVIDER);
		}

		stylistRepository.save(stylist);

		messagingService.sendMessage(MessagingService.BOOKING_EXCHANGE,
				MessagingService.BOOKINGS_STYLISTS_SERVICE_PROVIDER_DELETE_ROUTING_KEY,
				serviceProviderId, Map.of("stylistId", stylistId));
	}

	public String addServiceSpecification(String stylistId, StylistServiceSpecification serviceSpecification) {
		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));

		serviceSpecification.setId(UUID.randomUUID().toString());

		stylist.getServiceSpecifications().add(serviceSpecification);
		stylistRepository.save(stylist);

		messagingService.sendMessage(MessagingService.BOOKING_EXCHANGE,
				MessagingService.BOOKINGS_STYLISTS_SERVICE_SPECIFICATION_NEW_ROUTING_KEY,
				GlamoorJsonMapper.toJson(serviceSpecification));

		return serviceSpecification.getId();
	}

	public void updateServiceSpecification(String stylistId,
			StylistServiceSpecification serviceSpecification) {

		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));

		List<StylistServiceSpecification> serviceSpecifications = stylist.getServiceSpecifications();

		boolean found = false;

	    for (int i = 0; i < serviceSpecifications.size(); i++) {
	        if (serviceSpecifications.get(i).getId().equals(serviceSpecification.getId())) {
	        	serviceSpecifications.set(i, serviceSpecification);
	            found = true;
	            break;
	        }
	    }

	    if (!found) {
	        throw new EntityNotFoundException(serviceSpecification.getId(), EntityType.SERVICE_SPECIFICATION);
	    }

		stylistRepository.save(stylist);

		messagingService.sendMessage(MessagingService.BOOKING_EXCHANGE,
				MessagingService.BOOKINGS_STYLISTS_SERVICE_SPECIFICATION_UPDATE_ROUTING_KEY,
				GlamoorJsonMapper.toJson(serviceSpecification), Map.of("stylistId", stylistId));
	}

	public void removeServiceSpecification(String stylistId, String serviceSpecificationId) {
		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));

		boolean removed = stylist.getServiceSpecifications().removeIf(spec -> spec.getId().equals(serviceSpecificationId));
		if (!removed) {
		    throw new EntityNotFoundException(serviceSpecificationId, EntityType.SERVICE_SPECIFICATION);
		}

		stylistRepository.save(stylist);

		messagingService.sendMessage(MessagingService.BOOKING_EXCHANGE,
				MessagingService.BOOKINGS_STYLISTS_SERVICE_SPECIFICATION_DELETE_ROUTING_KEY,
				serviceSpecificationId, Map.of("stylistId", stylistId));
	}

	public String addHomeServiceSpecification(String stylistId, HomeServiceSpecification homeServiceSpecification) {
		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));

		homeServiceSpecification.setId(UUID.randomUUID().toString());

		stylist.getHomeServiceSpecifications().add(homeServiceSpecification);
		stylistRepository.save(stylist);

		messagingService.sendMessage(MessagingService.BOOKING_EXCHANGE,
				MessagingService.BOOKINGS_STYLISTS_HOME_SERVICE_SPECIFICATION_NEW_ROUTING_KEY,
				GlamoorJsonMapper.toJson(homeServiceSpecification));

		return homeServiceSpecification.getId();
	}

	public void updateHomeServiceSpecification(String stylistId,
			HomeServiceSpecification homeServiceSpecification) {

		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));

		List<HomeServiceSpecification> homeServiceSpecifications = stylist.getHomeServiceSpecifications();

		boolean found = false;

	    for (int i = 0; i < homeServiceSpecifications.size(); i++) {
	        if (homeServiceSpecifications.get(i).getId().equals(homeServiceSpecification.getId())) {
	        	homeServiceSpecifications.set(i, homeServiceSpecification);
	            found = true;
	            break;
	        }
	    }

	    if (!found) {
	        throw new EntityNotFoundException(homeServiceSpecification.getId(), EntityType.HOME_SERVICE_SPECIFICATION);
	    }

		stylistRepository.save(stylist);

		messagingService.sendMessage(MessagingService.BOOKING_EXCHANGE,
				MessagingService.BOOKINGS_STYLISTS_HOME_SERVICE_SPECIFICATION_UPDATE_ROUTING_KEY,
				GlamoorJsonMapper.toJson(homeServiceSpecification), Map.of("stylistId", stylistId));
	}

	public void removeHomeServiceSpecification(String stylistId, String homeServiceSpecificationId) {
		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));

		boolean removed = stylist.getHomeServiceSpecifications().removeIf(spec -> spec.getId().equals(homeServiceSpecificationId));
		if (!removed) {
		    throw new EntityNotFoundException(homeServiceSpecificationId, EntityType.HOME_SERVICE_SPECIFICATION);
		}

		stylistRepository.save(stylist);

		messagingService.sendMessage(MessagingService.BOOKING_EXCHANGE,
				MessagingService.BOOKINGS_STYLISTS_HOME_SERVICE_SPECIFICATION_DELETE_ROUTING_KEY,
				homeServiceSpecificationId, Map.of("stylistId", stylistId));
	}



	public List<Stylist> getStylists() {

		return stylistRepository.findAll(PageRequest.of(0, 5)).getContent();
	}

	public void validateRequestSender(String id1, String id2) {
		if (id1 == null || !id1.equals(id2)) {
			throw new IllegalArgumentException("Unauthorised access.");
		}
	}
}
