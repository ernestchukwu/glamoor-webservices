package uk.co.glamoor.customers.controller;

import java.util.UUID;

import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import uk.co.glamoor.customers.config.AppConfig;
import uk.co.glamoor.customers.dto.messaging.request.customer.CustomerRequestToBookings;
import uk.co.glamoor.customers.dto.request.CustomerRequest;
import uk.co.glamoor.customers.dto.response.CustomerResponse;
import uk.co.glamoor.customers.dto.response.UpdateCustomerResponse;
import uk.co.glamoor.customers.exception.EntityNotFoundException;
import uk.co.glamoor.customers.mapper.CustomerMapper;
import uk.co.glamoor.customers.mapper.GlamoorJsonMapper;
import uk.co.glamoor.customers.model.Customer;
import uk.co.glamoor.customers.enums.CustomerStatus;
import uk.co.glamoor.customers.service.CustomerService;
import uk.co.glamoor.customers.service.FileService;
import uk.co.glamoor.customers.service.MessagingService;
import uk.co.glamoor.customers.service.api.GatewayService;

@RestController
@RequestMapping("/api/customers")
@Validated
@RequiredArgsConstructor
public class CustomerController {

	private final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	private final CustomerService customerService;
	private final MessagingService messagingService;
	private final GatewayService gatewayService;
	private final FileService fileService;
	private final AppConfig appConfig;

	@GetMapping
	public Mono<ResponseEntity<?>> getCustomerByUid(
			@RequestParam @NotBlank(message = "uid must be provided.") String uid,
			@RequestParam(required = false, defaultValue = "false") boolean checkIn) {

		return customerService.getCustomerByUidOrCreateCustomerByUid(uid, checkIn)
				.flatMap(customer -> {
					if (customer.getStatus() != CustomerStatus.ACTIVE) {
						return Mono.just(ResponseEntity.notFound().build());
					}
					return Mono.just(ResponseEntity.ok(CustomerMapper.toCustomerResponse(customer)));
				})
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
	}

	@GetMapping("/{customerId}/bookings-service")
	public Mono<ResponseEntity<CustomerRequestToBookings>> getCustomer(
			@PathVariable @NotBlank String customerId) {

		return customerService.getCustomerById(customerId)
				.map(customer -> ResponseEntity.ok(CustomerMapper.toCustomerRequestToBookings(customer)))
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
				.onErrorResume(EntityNotFoundException.class, e ->
						Mono.just(ResponseEntity.notFound().build()));
	}

	@PostMapping
	public Mono<ResponseEntity<CustomerResponse>> saveCustomer(
			@RequestBody @Valid CustomerRequest customerRequest) {

		return customerService.saveCustomer(customerRequest)
				.flatMap(customer -> gatewayService.addUser(customer)
                        .thenReturn(customer)
                        .onErrorResume(e -> {
                            logger.error("Failed to add user to gateway, proceeding anyway", e);
                            return Mono.just(customer); // Continue flow even if gateway fails
                        }))
				.flatMap(customer -> {
					if (customer.getPaymentCustomerId() != null &&
							!customer.getPaymentCustomerId().isEmpty()) {
						return Mono.just(customer);
					}
					return customerService.addPaymentCustomerId(customer)
							.doOnNext(this::sendBookingUpdate)
							.defaultIfEmpty(customer);
				})
				.map(updated -> ResponseEntity.ok(CustomerMapper.toCustomerResponse(updated)))
				.switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()))
				.onErrorResume(e -> {
					logger.error("Customer creation failed", e);
					return Mono.just(ResponseEntity.internalServerError().build());
				});
	}

	private void sendBookingUpdate(Customer customer) {
		Mono.fromRunnable(() -> {
					String message = GlamoorJsonMapper.toJson(
							CustomerMapper.toCustomerRequestToBookings(customer)
					);
					messagingService.sendMessage(
							MessagingService.BOOKING_EXCHANGE,
							MessagingService.BOOKINGS_CUSTOMERS_UPDATE_ROUTING_KEY,
							message
					);
				})
				.subscribeOn(Schedulers.boundedElastic())
				.subscribe(
						null,
						e -> logger.error("Failed to send booking update", e),
						() -> logger.debug("Booking update sent")
				);
	}
	
	@GetMapping("/sign-in-method")
	public Mono<ResponseEntity<String>> getSignInMethod(
			@RequestParam @Email String email) {
		
		return customerService.getAccountProviderByEmail(email.toLowerCase())
				.flatMap(provider -> {
					return Mono.just(ResponseEntity.ok(provider));
				})
				.switchIfEmpty(
						 Mono.just(ResponseEntity.notFound().build())
				);
	}



	@PatchMapping("/{customerId}/default-address/{addressId}")
	public Mono<ResponseEntity<Void>> setDefaultAddress(
			@RequestHeader(value = "X-User-Id", required = false) String id,
			@PathVariable @NotBlank String customerId,
			@PathVariable @NotBlank String addressId) {

		return customerService.validateRequestSender(id, customerId)
				.then(customerService.setDefaultAddress(customerId, addressId))
				.then(Mono.just(ResponseEntity.noContent().<Void>build()))  // Explicit type parameter
				.onErrorResume(EntityNotFoundException.class, e ->
						Mono.just(ResponseEntity.notFound().<Void>build()))
				.onErrorResume(e ->
						Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<Void>build()));
	}

	@PatchMapping("/{customerId}")
	public Mono<ResponseEntity<UpdateCustomerResponse>> updateCustomerDetails(
			@RequestHeader(value = "X-User-Id", required = false) String id,
			@PathVariable @NotBlank String customerId,
			@RequestBody @Valid CustomerRequest customerRequest) {

		return customerService.validateRequestSender(id, customerId)
				.then(Mono.fromCallable(() -> CustomerMapper.toCustomer(customerRequest)))
				.subscribeOn(Schedulers.boundedElastic())
				.flatMap(update -> customerService.updateCustomerDetails(customerId, update))
				.map(response -> {
					logger.debug("Returning response: {}", response);
					return ResponseEntity.ok(response);
				})
				.doOnNext(responseEntity ->
						logger.debug("Response entity: {}", responseEntity.getBody()))
				.subscribeOn(Schedulers.boundedElastic());
	}


	@PatchMapping("/{customerId}/default-address")
	public Mono<ResponseEntity<?>> setCustomerDefaultAddress(
			@RequestHeader(value = "X-User-Id", required = false) String id,
			@PathVariable @NotBlank String customerId,
			@RequestParam @Size(max = 100, min = 5) String addressId) {

		return customerService.validateRequestSender(id, customerId)
				.then(customerService.setCustomerDefaultAddress(customerId, addressId))
				.thenReturn(ResponseEntity.noContent().build());

	}

	@PostMapping("/{customerId}/profile-picture")
	public Mono<ResponseEntity<String>> uploadImage(
			@RequestHeader(value = "X-User-Id", required = false) String id,
			@PathVariable String customerId,
			@RequestPart("file") FilePart file) {

		return customerService.validateRequestSender(id, customerId)
				.flatMap(valid -> {
					final String profilePicturesDir = appConfig.getImages().getDirectories().getUserProfilePictures();
					final String fileName = customerId + "_" + UUID.randomUUID().toString().substring(0, 5) +
							fileService.getFileExtension(file);
					return fileService.deleteExistingProfilePicture(customerId, profilePicturesDir)
							.then(fileService.saveStaticFile(file, fileName, profilePicturesDir))
							.then(customerService.updateCustomerProfilePicture(customerId, fileName))
							.thenReturn(ResponseEntity.ok(fileName));
				})
				.onErrorResume(e -> Mono.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())
                ));


	}
}
