package uk.co.glamoor.bookings.service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import uk.co.glamoor.bookings.config.AppConfig;
import uk.co.glamoor.bookings.dto.request.BookingRequest;
import uk.co.glamoor.bookings.enums.BookingStatus;
import uk.co.glamoor.bookings.exception.EntityNotFoundException;
import uk.co.glamoor.bookings.exception.EntityType;
import uk.co.glamoor.bookings.mapper.*;
import uk.co.glamoor.bookings.model.*;
import uk.co.glamoor.bookings.model.AvailabilitySlot;
import uk.co.glamoor.bookings.repository.BookingCancellationRepository;
import uk.co.glamoor.bookings.repository.CustomerRepository;
import uk.co.glamoor.bookings.repository.StylistRepository;
import uk.co.glamoor.bookings.service.api.CustomersAPIService;
import uk.co.glamoor.bookings.service.api.StylistsAPIService;
import uk.co.glamoor.bookings.repository.BookingRepository;

@Service
public class BookingService {
	
	private final CustomerRepository customerRepository;
	private final StylistRepository stylistRepository;
	private final BookingRepository bookingRepository;
	private final BookingCancellationRepository bookingCancellationRepository;
	private final AvailabilityService availabilityService;
	private final CustomersAPIService customersAPIService;
	private final StylistsAPIService stylistsAPIService;
	private final MessagingService messagingService;
	private final AppConfig appConfig;
	
	public BookingService(BookingRepository bookingRepository, 
			BookingCancellationRepository bookingCancellationRepository,
			CustomerRepository customerRepository,
			StylistRepository stylistRepository,
			AvailabilityService availabilityService,
			StylistsAPIService StylistsAPIService,
			CustomersAPIService customersAPIService,
			MessagingService messagingService,
			AppConfig appConfig) {
		
		this.bookingRepository = bookingRepository;
		this.bookingCancellationRepository = bookingCancellationRepository;
		this.customerRepository = customerRepository;
		this.stylistRepository = stylistRepository;
		this.availabilityService = availabilityService;
		this.customersAPIService = customersAPIService;
		this.stylistsAPIService = StylistsAPIService;
		this.messagingService = messagingService;
		this.appConfig = appConfig;
		
	}
	
	public List<Booking> getBookings(int offset, String customerId, BookingStatus status, boolean homeScreenView) {
		
		int batchSize = homeScreenView ? appConfig.getBookingsRequestBatchSizeForHomeScreen() :
				appConfig.getBookingsRequestBatchSize();
		Pageable pageable = PageRequest.of(homeScreenView ? 0 : offset, batchSize);

		return status == null ?
				bookingRepository.findByCustomerIdOrderByTimeDesc(customerId, pageable).getContent() :
				bookingRepository.findByCustomerIdAndStatusOrderByTimeDesc(customerId,
				status.toString(), pageable).getContent();
		
	}
	
	public Booking getBooking(String customerId, String bookingId) {
		
		return bookingRepository.findByCustomerIdAndId(customerId, bookingId);
		
	}

	public Optional<Booking> getBooking(String bookingId) {

		return bookingRepository.findById(bookingId);

	}

	public void cancelBooking(String bookingId, String customerId, String reason, String reasonDetails, String timeZone) {
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(bookingId, EntityType.BOOKING));

        if (Duration.between(Instant.now(), booking.getTimeUtc().toInstant(ZoneOffset.UTC)).toMinutes() < booking.getStylist().getBookingCancellationTimeLimitMinutes()) {
        	LocalDateTime windowEnd = booking.getTimeUtc().minusMinutes(booking.getStylist().getBookingCancellationTimeLimitMinutes());
        	throw new IllegalArgumentException("Booking can no longer be canceled. Cancellation window closed at " + windowEnd);
        }

		if (!booking.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Customer is not authorized to cancel this booking.");
        }
        
        if (BookingStatus.CANCELED.equals(booking.getStatus())) {
            throw new IllegalArgumentException("Booking is already canceled.");
        }
        
        if (reason == null) {
        	throw new IllegalArgumentException("Reason is required to cancel booking.");
        }
        
        BookingCancellation bookingCancellation = new BookingCancellation();
        bookingCancellation.setBooking(booking);
        BookingCancellationReason bookingCancellationReason = new BookingCancellationReason();
        bookingCancellationReason.setReason(reason);
        bookingCancellation.setBookingCancellationReason(bookingCancellationReason);
        bookingCancellation.setOtherReason(reasonDetails);
        
        booking.setStatus(BookingStatus.CANCELED);
                        
        availabilityService.unbookSlot(booking.getStylist().getId(), 
        		booking.getServiceProvider().getId(), booking.getTimeUtc(), booking.getTotalDuration(), timeZone);
        
        bookingCancellationRepository.save(bookingCancellation);
        
        bookingRepository.save(booking);
        
        messagingService.sendMessage(MessagingService.NOTIFICATION_EXCHANGE,
        		MessagingService.NOTIFICATIONS_BOOKINGS_CANCELLED_ROUTING_KEY, 
        		GlamoorJsonMapper.toJson(booking));
        
    }

	public Mono<Booking> saveBooking(Booking booking) {
		return Mono.just(bookingRepository.save(booking));
	}

	public void addPayment(String bookingId, Payment payment, String timeZone) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Could not find booking with id: " + bookingId));

		booking.getPayments().add(payment);
		booking.setStatus(BookingStatus.CONFIRMED);
		bookingRepository.save(booking);

		if (booking.getPayments().size() == 1) {
			availabilityService.bookEscrowedSlot(booking.getStylist().getId(),
					booking.getServiceProvider().getId(), booking.getTimeUtc(),
					booking.getTotalDuration(), timeZone);
			sendBookingConfirmationMessages(booking);
		}
	}
	
	public void sendBookingConfirmationMessages(Booking booking) {

		AvailabilitySlot slot = new AvailabilitySlot();
		slot.setStylistId(booking.getStylist().getId());
		slot.setServiceProviderId(booking.getServiceProvider().getId());
		slot.setDate(booking.getTimeUtc().toLocalDate());
		slot.setTimeSlot(new Availability.TimeSlot(booking.getTimeUtc().toLocalTime(),
				booking.getTimeUtc().toLocalTime().plusMinutes(booking.getTotalDuration())));

		messagingService.sendMessage(MessagingService.NOTIFICATION_EXCHANGE,
				MessagingService.NOTIFICATIONS_BOOKINGS_NEW_ROUTING_KEY,
				GlamoorJsonMapper.toJson(BookingMapper.toBookingMessage(booking)));

		messagingService.sendMessage(MessagingService.STYLIST_EXCHANGE,
				MessagingService.STYLISTS_AVAILABILITIES_BOOK_ROUTING_KEY,
				GlamoorJsonMapper.toJson(slot),
				Map.of("stylistId", booking.getStylist().getId(),
						"date", booking.getTimeUtc().format(DateTimeFormatter.ISO_DATE_TIME),
						"serviceProviderId", booking.getServiceProvider().getId()));
	}
	
	public boolean isBookingCustomer(String customerId, String bookingId) {
		return bookingRepository.existsByIdAndCustomerId(bookingId, customerId);
	}

	public Mono<Booking> validateBookingRequestAndGenerateBooking(BookingRequest bookingRequest) {

		return Mono.defer(() -> {
			Mono<Customer> customerMono = retrieveCustomerDetails(bookingRequest.getCustomerId());

			Mono<Stylist> stylistMono = retrieveStylistDetails(bookingRequest.getStylistId());

			return customerMono.zipWith(stylistMono)
					.flatMap(tuple -> {
						Customer customer = tuple.getT1();
						Stylist stylist = tuple.getT2();

						Booking booking = new Booking();
						booking.setCustomer(BookingMapper.toBookingCustomer(customer));
						booking.setStylist(BookingMapper.toBookingStylist(stylist));
						booking.setCurrency(stylist.getCurrency());
						booking.setTimeZone(bookingRequest.getTimeZone());
						booking.setTime(bookingRequest.getTime().atZone(
								ZoneId.of(bookingRequest.getTimeZone())).toInstant());
						booking.setBookingReference(generateReference());
						booking.setAddress(bookingRequest.getHomeServiceSpecificationId() != null ?
								BookingMapper.toAddress(bookingRequest.getAddress()) :
								stylist.getAddress());
						booking.setNotes(bookingRequest.getNotes());
						booking.setLocation(bookingRequest.getHomeServiceSpecificationId() != null ?
								BookingMapper.toLocation(bookingRequest.getLocation()) :
								stylist.getLocation());
						booking.setHomeService(bookingRequest.getHomeServiceSpecificationId() != null);

						return processHomeServiceBooking(booking, bookingRequest.getHomeServiceSpecificationId(),
								stylist).flatMap(
								booking_ -> processServiceProvider(booking_,
										bookingRequest.getServiceProviderId(), stylist).flatMap(
                                        booking__ -> processServiceSpecifications(booking__,
												bookingRequest, stylist)
                                )
						);
					});
		});
	}

	private Mono<Stylist> retrieveStylistDetails(String stylistId) {
		return Mono.fromCallable(() ->
						stylistRepository.findById(stylistId)
				).subscribeOn(Schedulers.boundedElastic())
				.flatMap(optionalStylist -> optionalStylist
						.map(Mono::just)
						.orElseGet(() ->
								stylistsAPIService.getStylist(stylistId)
										.flatMap(stylist -> {
											if (stylist == null) {
												return Mono.error(new EntityNotFoundException(
														"Stylist with id: '" + stylistId + "' not found."));
											}
											return Mono.fromCallable(() -> stylistRepository.save(stylist))
													.subscribeOn(Schedulers.boundedElastic());
										})
						)
				);
	}

	private Mono<Customer> retrieveCustomerDetails(String customerId) {
		return Mono.fromCallable(() ->
						customerRepository.findById(customerId)
				).subscribeOn(Schedulers.boundedElastic())
				.flatMap(optionalCustomer -> optionalCustomer
							.map(Mono::just)
							.orElseGet(() ->
									customersAPIService.getCustomer(customerId)
											.flatMap(customer -> {
												if (customer == null) {
													return Mono.error(new EntityNotFoundException(
															"Customer with id: '" + customerId + "' not found."));
												}
												return Mono.fromCallable(() -> customerRepository.save(customer))
														.subscribeOn(Schedulers.boundedElastic());
											})
							)
				);
	}

	private Mono<Booking> processHomeServiceBooking(Booking booking, String homeServiceSpecificationId, Stylist stylist) {
		if (!booking.isHomeService()) {
			return Mono.just(booking);
		}

		List<HomeServiceSpecification> homeServiceSpecifications = stylist
				.getHomeServiceSpecifications().stream()
				.filter(spec ->
						spec.getId().equals(homeServiceSpecificationId)).toList();

		if (!homeServiceSpecifications.isEmpty()) {
			booking.setHomeServiceSpecification(homeServiceSpecifications.get(0));
			return Mono.just(booking);
		}

		return stylistsAPIService.getStylistHomeServiceSpecification(
				booking.getStylist().getId(), homeServiceSpecificationId)
				.switchIfEmpty(Mono.error(new RuntimeException(
						"Could not find home service spec '" + homeServiceSpecificationId +
								"' in stylist(" + stylist.getId() + ") home service specifications.")))
				.doOnNext(spec -> {
					stylist.getHomeServiceSpecifications().add(spec);
					stylistRepository.save(stylist);
					booking.setHomeServiceSpecification(spec);
				})
				.thenReturn(booking);
	}

	private Mono<Booking> processServiceProvider(Booking booking, String serviceProviderId, Stylist stylist) {

		List<ServiceProvider> serviceProviders = stylist
				.getServiceProviders().stream()
				.filter(serviceProvider ->
						serviceProvider.getId().equals(serviceProviderId)).toList();

		if (!serviceProviders.isEmpty()) {
			booking.setServiceProvider(ServiceProviderMapper.toBookingServiceProvider(serviceProviders.get(0)));
			return Mono.just(booking);
		}

		return stylistsAPIService.getStylistServiceProvider(
						booking.getStylist().getId(), serviceProviderId)
				.switchIfEmpty(Mono.error(new RuntimeException(
						"Could not find service provider '" + serviceProviderId +
								"' in stylist(" + serviceProviderId + ")")))
				.doOnNext(serviceProvider -> {
					stylist.getServiceProviders().add(serviceProvider);
					stylistRepository.save(stylist);
					booking.setServiceProvider(ServiceProviderMapper.toBookingServiceProvider(serviceProvider));
				}).thenReturn(booking);
	}

	private Mono<Booking> processServiceSpecifications(Booking booking, BookingRequest bookingRequest, Stylist stylist) {
		return Flux.fromIterable(bookingRequest.getServiceSpecifications())
				.flatMap(spec -> processStylistServiceSpecification(spec, stylist))
				.collectList()
				.doOnNext(booking::setServiceSpecifications)
				.thenReturn(booking);
	}

	private Mono<Booking.StylistServiceSpecification> processStylistServiceSpecification(
			BookingRequest.StylistServiceSpecification spec, Stylist stylist) {

		return findServiceSpecification(stylist, spec.getId())
				.flatMap(stylistServiceSpec -> {
					Booking.StylistServiceSpecification serviceSpecification = new Booking.StylistServiceSpecification();
					serviceSpecification.setId(spec.getId());
					serviceSpecification.setHomeServiceAdditionalPrice(stylistServiceSpec.getHomeServiceAdditionalPrice());
					serviceSpecification.setDepositPaymentPercent(stylistServiceSpec.getDepositPaymentPercent());

					return validateServiceOption(stylistServiceSpec, spec.getOptionId())
							.doOnNext(option -> {
								serviceSpecification.setOption(StylistServiceSpecificationMapper
										.toBookingServiceSpecificationOption(option));
							})

							.thenMany(processAddonSpecifications(stylistServiceSpec, spec.getAddonSpecifications()))
							.collectList()
							.doOnNext(serviceSpecification::setAddonSpecifications)
							.doOnNext(ignored -> mapStylistServiceDetails(stylistServiceSpec, serviceSpecification))
							.thenReturn(serviceSpecification);
				});
	}

	private Mono<StylistServiceSpecification> findServiceSpecification(Stylist stylist, String specId) {
		return Mono.justOrEmpty(
						stylist.getServiceSpecifications()
								.stream()
								.filter(s -> s.getId().equals(specId))
								.findFirst()
				)
				.switchIfEmpty(
					stylistsAPIService.getStylistServiceSpecification(stylist.getId(), specId)
							.flatMap(fetchedSpec -> {
								stylist.getServiceSpecifications().add(fetchedSpec);
								stylistRepository.save(stylist);
								return Mono.just(fetchedSpec);
							})
				)
				.switchIfEmpty(Mono.error(new RuntimeException("Service specifications mismatch.")));
	}


	private Mono<ServiceSpecification.ServiceSpecificationOption> validateServiceOption(
			StylistServiceSpecification serviceSpec, String optionId) {
		return Mono.justOrEmpty(serviceSpec.getOptions()
						.stream().filter(o -> o.getId().equals(optionId)).findFirst())
				.switchIfEmpty(Mono.error(new RuntimeException("Service specifications option mismatch.")));
	}

	private Mono<AddonSpecification.ServiceSpecificationOption> validateServiceOption(
			AddonSpecification addonSpec, String optionId) {
		return Mono.justOrEmpty(addonSpec.getOptions()
						.stream().filter(o -> o.getId().equals(optionId)).findFirst())
				.switchIfEmpty(Mono.error(new RuntimeException("Addon specifications option mismatch.")));
	}

	private Flux<Booking.AddonSpecification> processAddonSpecifications(
			StylistServiceSpecification stylistServiceSpec, List<BookingRequest.ServiceSpecification> addonSpecs) {
		return Flux.fromIterable(addonSpecs)
				.flatMap(addonSpec -> findAddonSpecification(stylistServiceSpec, addonSpec));
	}

	private Mono<Booking.AddonSpecification> findAddonSpecification(
			StylistServiceSpecification stylistServiceSpec, BookingRequest.ServiceSpecification addonSpec) {
		return Mono.justOrEmpty(stylistServiceSpec.getAddonSpecifications()
						.stream().filter(s -> s.getId().equals(addonSpec.getId())).findFirst())
				.switchIfEmpty(Mono.error(new RuntimeException("Addon specifications mismatch.")))
				.flatMap(addonSpecification ->
						validateServiceOption(addonSpecification, addonSpec.getOptionId())
						.map(option -> mapAddonSpecification(addonSpecification, option))
				);
	}

	private Booking.AddonSpecification mapAddonSpecification(
			AddonSpecification addonSpec, ServiceSpecification.ServiceSpecificationOption option) {
		Booking.AddonSpecification addonSpecification = new Booking.AddonSpecification();
		addonSpecification.setId(addonSpec.getId());
		addonSpecification.setOption(StylistServiceSpecificationMapper.toBookingServiceSpecificationOption(option));
		addonSpecification.setAddon(addonSpec.getAddon());
		addonSpecification.setNote(addonSpec.getNote());
		addonSpecification.setTerms(addonSpec.getTerms());
		addonSpecification.setHomeServiceAdditionalPrice(addonSpec.getHomeServiceAdditionalPrice());
		addonSpecification.setHomeServiceAvailable(addonSpec.getHomeServiceAvailable());
		return addonSpecification;
	}

	private void mapStylistServiceDetails(
			StylistServiceSpecification serviceSpec, Booking.StylistServiceSpecification bookingSpec) {
		bookingSpec.setService(serviceSpec.getService());
		bookingSpec.setNote(serviceSpec.getNote());
		bookingSpec.setTerms(serviceSpec.getTerms());
		bookingSpec.setHomeServiceAvailable(serviceSpec.getHomeServiceAvailable());
	}


	private String generateReference() {
        return "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
	
}
