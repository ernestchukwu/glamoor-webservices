package uk.co.glamoor.stylists.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.github.javafaker.Faker;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import uk.co.glamoor.stylists.config.AppConfig;
import uk.co.glamoor.stylists.dto.StylistDTO;
import uk.co.glamoor.stylists.dto.response.StylistSummaryResponse;
import uk.co.glamoor.stylists.dto.response.bookings.BookingsServiceHomeServiceSpecificationResponse;
import uk.co.glamoor.stylists.dto.response.bookings.BookingsServiceProviderResponse;
import uk.co.glamoor.stylists.dto.response.bookings.BookingsServiceStylistServiceSpecificationResponse;
import uk.co.glamoor.stylists.enums.OrderOption;
import uk.co.glamoor.stylists.exception.EntityNotFoundException;
import uk.co.glamoor.stylists.exception.EntityType;
import uk.co.glamoor.stylists.mapper.*;
import uk.co.glamoor.stylists.model.*;
import uk.co.glamoor.stylists.dto.response.bookings.BookingsServiceStylistResponse;
import uk.co.glamoor.stylists.repository.StylistRepository;
import uk.co.glamoor.stylists.service.LocationService;
import uk.co.glamoor.stylists.service.RequestService;
import uk.co.glamoor.stylists.service.StylistService;

@RestController
@RequestMapping("/api/stylists")
@Validated
public class StylistController {
	
	private final StylistService stylistService;
	private final StylistRepository stylistRepository;
	private final LocationService locationService;
	private final RequestService requestService;
	private final AppConfig appConfig;

	public StylistController(StylistService stylistService,
                             StylistRepository stylistRepository,
                             LocationService locationService,
                             RequestService requestService,
                             AppConfig appConfig) {
		
		this.stylistService = stylistService;
        this.stylistRepository = stylistRepository;
        this.locationService = locationService;
        this.requestService = requestService;
        this.appConfig = appConfig;
    }

	private Location getLocation(ServerWebExchange exchange, Double lat, Double lng) {
		if (lat == null || lng == null) {
			double[] coordinates = locationService.getLocation(requestService.getIPAddress(exchange));
			if (coordinates != null) {
				lng = coordinates[0];
				lat = coordinates[1];
			} else {
				lng = appConfig.getDefaultLongitude();
				lat = appConfig.getDefaultLatitude();
			}
		}

		Location location = new Location();
		location.setCoordinates(List.of(lng, lat));

		return location;
	}

	@GetMapping("/suggested")
	public ResponseEntity<List<StylistSummaryResponse>> getSuggestedStylists(
			ServerWebExchange exchange,
			@RequestParam(required = false) @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90")
			@DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90")
			Double lat,
			@RequestParam(required = false) @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180")
			@DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180")
			Double lng,
			@RequestParam(defaultValue = "0") @Min(value=0) int offset,
			@RequestParam(required = false) String customerId,
			@RequestParam boolean homeView) {

		Location location = getLocation(exchange, lat, lng);

		List<Stylist> stylists = stylistService.getSuggestedStylists(
				location, customerId, offset, homeView);
		
		return ResponseEntity.ok(stylists.stream().map(
				StylistMapper::toStylistSummaryResponse).toList());
	}
	
	@GetMapping("/featured")
	public ResponseEntity<List<StylistSummaryResponse>> getFeaturedStylists(
			ServerWebExchange exchange,
			@RequestParam(required = false) @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90")
			@DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90")
			Double lat,
			@RequestParam(required = false) @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180")
			@DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180")
			Double lng,
			@RequestParam @Min(value=0) int offset,
			@RequestParam(required = false) String customerId,
			@RequestParam boolean homeView) {

		Location location = getLocation(exchange, lat, lng);

		List<Stylist> stylists = stylistService.getFeaturedStylists(
				location, customerId, offset, homeView);
		
		return ResponseEntity.ok(stylists.stream().map(
                        StylistMapper::toStylistSummaryResponse)
				.toList());
	}

	@GetMapping("/favourites/{customerId}")
	public ResponseEntity<List<StylistDTO>> getFavouriteStylists(
			@RequestHeader(value = "X-User-Id", required = false) String id,
			@PathVariable @NotBlank String customerId,
			@RequestParam(value = "order", defaultValue = "DEFAULT") OrderOption order,
			@RequestParam(required = false) String searchString,
			@RequestParam(value = "offset", defaultValue = "0") @Min(value = 0) int offset) {

		stylistService.validateRequestSender(id, customerId);
		return ResponseEntity.ok(stylistService.getFavouriteStylistsForCustomer(customerId, order, offset, searchString)
				.stream().map(
						stylist -> StylistMapper.toDto(stylist, MapperType.MIN))
				.toList());
	}
	
	@GetMapping("/{stylistId}")
	public ResponseEntity<StylistDTO> getStylist(
			@PathVariable @NotBlank String stylistId,
			@RequestParam(required = false) String customerId) {

		return ResponseEntity.ok(StylistMapper
				.toDto(stylistService.getStylistById(stylistId, customerId), MapperType.MAX));
				
	}

	@GetMapping("/{stylistId}/bookings-service")
	public ResponseEntity<BookingsServiceStylistResponse> getStylistForBookingsService(
			@PathVariable @NotBlank String stylistId) {

		return ResponseEntity.ok(StylistMapper
				.toBookingsServiceStylist(stylistService.getStylistById(stylistId)));

	}

	@GetMapping("/{stylistId}/service-specifications/{serviceSpecificationId}/bookings-service")
	public ResponseEntity<BookingsServiceStylistServiceSpecificationResponse> getStylistServiceSpecification(
			@PathVariable @NotBlank String stylistId,
			@PathVariable @NotBlank String serviceSpecificationId) {

		return ResponseEntity.ok(stylistService.getStylistById(stylistId)
				.getServiceSpecifications().stream()
				.filter(spec -> spec.getId()
						.equals(serviceSpecificationId)).findFirst()
						.map(ServiceSpecificationMapper::toBookingsServiceStylistServiceSpecificationResponse)
				.orElseThrow(() ->
						new EntityNotFoundException(serviceSpecificationId, EntityType.SERVICE_SPECIFICATION)));

	}

	@GetMapping("/{stylistId}/home-service-specifications/{homeServiceSpecificationId}/bookings-service")
	public ResponseEntity<BookingsServiceHomeServiceSpecificationResponse> getStylistHomeServiceSpecification(
			@PathVariable @NotBlank String stylistId,
			@PathVariable @NotBlank String homeServiceSpecificationId) {

		return ResponseEntity.ok(stylistService.getStylistById(stylistId)
				.getHomeServiceSpecifications().stream()
				.filter(spec -> spec.getId()
						.equals(homeServiceSpecificationId)).findFirst()
				.map(HomeServiceSpecificationMapper::toBookingsServiceHomeServiceSpecificationResponse)
				.orElseThrow(() ->
						new EntityNotFoundException(homeServiceSpecificationId, EntityType.HOME_SERVICE_SPECIFICATION)));

	}

	@GetMapping("/{stylistId}/service-providers/{serviceProviderId}/bookings-service")
	public ResponseEntity<BookingsServiceProviderResponse> getStylistServiceProvider(
			@PathVariable @NotBlank String stylistId,
			@PathVariable @NotBlank String serviceProviderId) {

		return ResponseEntity.ok(stylistService.getStylistById(stylistId)
				.getServiceProviders().stream()
				.filter(spec -> spec.getId()
						.equals(serviceProviderId)).findFirst()
						.map(ServiceProviderMapper::toBookingsServiceServiceProviderResponse)
				.orElseThrow(() ->
						new EntityNotFoundException(serviceProviderId, EntityType.SERVICE_PROVIDER)));

	}

	@GetMapping("/recently-viewed")
	public ResponseEntity<List<StylistSummaryResponse>> getRecentlyViewedStylists(
			@RequestHeader(value = "X-User-Id", required = false) String id,
			@RequestParam String customerId) {

		stylistService.validateRequestSender(id, customerId);

		List<Stylist> stylists = stylistService.getRecentlyViewedStylists(customerId);
		return ResponseEntity.ok(stylists.stream()
				.map(StylistMapper::toStylistSummaryResponse)
				.toList());
	}

	private final Faker faker = new Faker();
	public Phone generatePhone() {
		Phone phone = new Phone();

		phone.setNumber(faker.phoneNumber().phoneNumber());

		int countryCodeLength = faker.random().nextInt(2, 3);
		phone.setCountryCode("+" + faker.number().digits(countryCodeLength));

		phone.setCountryISOCode(faker.country().countryCode3().toUpperCase());

		return phone;
	}

	@PostMapping("/generate-data")
	public Mono<?> generateStylists() {
//		List<GlamoorService> services = glamoorServiceRepository.findAll();

		List<Stylist> stylists = stylistRepository.findAll();

		for(Stylist stylist : stylists) {
			stylist.setStatus(Status.ACTIVE);
		}
		stylistRepository.saveAll(stylists);
		return Mono.justOrEmpty("");
	}

	@PostMapping
	public ResponseEntity<StylistDTO> createStylist(
			@RequestBody @Valid StylistDTO stylistDto) {
		
		return ResponseEntity.ok(StylistMapper.toDto(
				stylistService.createStylist(StylistMapper.toStylist(stylistDto)), MapperType.MAX));
	}
	
	@PatchMapping
	public ResponseEntity<?> updateStylist(
			@RequestBody @Valid StylistDTO stylistDto) {
		
		stylistService.updateStylist(StylistMapper.toStylist(stylistDto));
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/{stylistId}")
	public ResponseEntity<?> anonymiseStylist(
			@PathVariable @NotNull @NotBlank String stylistId) {
		
		stylistService.anonymiseStylist(stylistId);
		
		return ResponseEntity.ok().build();
	}
	
	
	@PostMapping("/{stylistId}/service-providers")
	public ResponseEntity<String> addServiceProvider(
			@PathVariable @NotNull @NotBlank String stylistId,
			@RequestBody @Valid ServiceProvider serviceProvider) {
				
		return ResponseEntity.ok(stylistService.addServiceProvider(stylistId, serviceProvider));
	}
	
	@PatchMapping("/{stylistId}/service-providers")
	public ResponseEntity<?> updateServiceProvider(
			@PathVariable @NotNull @NotBlank String stylistId,
			@PathVariable @Valid ServiceProvider serviceProvider) {
		
		stylistService.updateServiceProvider(stylistId, serviceProvider);
		
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/{stylistId}/service-providers/{serviceProviderId}")
	public ResponseEntity<?> removeServiceProvider(
			@PathVariable @NotNull @NotBlank String stylistId,
			@PathVariable @NotNull @NotBlank String serviceProviderId) {
		
		stylistService.removeServiceProvider(stylistId, serviceProviderId);
		
		return ResponseEntity.ok().build();
	}
	
	
	@PostMapping("/{stylistId}/service-specifications")
	public ResponseEntity<String> addServiceSpecification(
			@PathVariable @NotNull @NotBlank String stylistId,
			@RequestBody @Valid StylistServiceSpecification serviceSpecification) {
		
		return ResponseEntity.ok(stylistService.addServiceSpecification(stylistId, serviceSpecification));
		
	}
	
	@PatchMapping("/{stylistId}/service-specifications")
	public ResponseEntity<?> updateServiceSpecification(
			@PathVariable @NotNull @NotBlank String stylistId,
			@PathVariable @Valid StylistServiceSpecification serviceSpecification) {
		
		stylistService.updateServiceSpecification(stylistId, serviceSpecification);
		
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/{stylistId}/service-specifications/{serviceSpecificationId}")
	public ResponseEntity<?> removeServiceSpecification(
			@PathVariable @NotNull @NotBlank String stylistId,
			@PathVariable @NotNull @NotBlank String serviceSpecificationId) {
		
		stylistService.removeServiceSpecification(stylistId, serviceSpecificationId);
		
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/{stylistId}/home-service-specifications")
	public ResponseEntity<String> addHomeServiceSpecification(
			@PathVariable @NotNull @NotBlank String stylistId,
			@RequestBody @Valid HomeServiceSpecification homeServiceSpecification) {
		
		return ResponseEntity.ok(stylistService.addHomeServiceSpecification(stylistId, homeServiceSpecification));
		
	}
	
	@PatchMapping("/{stylistId}/home-service-specifications")
	public ResponseEntity<?> updateHomeServiceSpecification(
			@PathVariable @NotNull @NotBlank String stylistId,
			@PathVariable @Valid HomeServiceSpecification homeServiceSpecification) {
		
		stylistService.updateHomeServiceSpecification(stylistId, homeServiceSpecification);
		
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/{stylistId}/home-service-specifications/{homeServiceSpecificationId}")
	public ResponseEntity<?> removeHomeServiceSpecification(
			@PathVariable @NotNull @NotBlank String stylistId,
			@PathVariable @NotNull @NotBlank String homeServiceSpecificationId) {
		
		stylistService.removeHomeServiceSpecification(stylistId, homeServiceSpecificationId);
		
		return ResponseEntity.ok().build();
	}
	
	
	
	@PostMapping("/{stylistId}/like")
	public ResponseEntity<?> likeStylist(
			@RequestHeader(value = "X-User-Id") String id,
			@PathVariable @NotBlank String stylistId,
			@RequestParam @NotBlank String customerId) {

		stylistService.validateRequestSender(id, customerId);
		stylistService.likeStylist(customerId, stylistId);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/{stylistId}/like")
	public ResponseEntity<?> unlikeStylist(
			@RequestHeader(value = "X-User-Id", required = false) String id,
			@PathVariable @NotBlank String stylistId,
			@RequestParam @NotBlank String customerId) {

		stylistService.validateRequestSender(id, customerId);
		stylistService.unlikeStylist(customerId, stylistId);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/search")
	public ResponseEntity<List<StylistDTO>> searchStylists(
			@RequestParam(required = false) String customerId,
			@RequestParam @NotBlank String searchText,
			@RequestParam @Min(value=0) int offset) {
		
		return ResponseEntity.ok(stylistService.
				search(searchText, offset, customerId).stream()
				.map(stylist -> StylistMapper.toDto(stylist, MapperType.MIN))
				.collect(Collectors.toList()));
	}
	
	@GetMapping("/{stylistId}/service-specifications")
	public ResponseEntity<List<StylistServiceSpecification>> getServiceSpecifications(
	    @PathVariable String stylistId, 
	    @RequestParam int offset, @RequestParam String serviceCategory) {

	    return ResponseEntity.ok(stylistService.getServiceSpecifications(stylistId, offset, serviceCategory));
	}
	
	@GetMapping("/{stylistId}/service-providers")
	public ResponseEntity<List<ServiceProvider>> getServiceProviders(
	    @PathVariable String stylistId) {

	    return ResponseEntity.ok(stylistService
	    		.getServiceProvidersForServices(stylistId));
	}

}
