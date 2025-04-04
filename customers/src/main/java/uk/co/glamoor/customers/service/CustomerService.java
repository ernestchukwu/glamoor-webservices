package uk.co.glamoor.customers.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import uk.co.glamoor.customers.dto.request.CustomerRequest;
import uk.co.glamoor.customers.dto.response.UpdateCustomerResponse;
import uk.co.glamoor.customers.enums.CustomerStatus;
import uk.co.glamoor.customers.exception.EntityNotFoundException;
import uk.co.glamoor.customers.enums.EntityType;
import uk.co.glamoor.customers.mapper.CustomerMapper;
import uk.co.glamoor.customers.mapper.GlamoorJsonMapper;
import uk.co.glamoor.customers.model.*;
import uk.co.glamoor.customers.dto.messaging.response.DefaultPaymentMethodResponseFromPayments;
import uk.co.glamoor.customers.repository.CustomerRepository;
import uk.co.glamoor.customers.service.api.GatewayService;
import uk.co.glamoor.customers.service.api.PaymentService;

@Service
public class CustomerService {

	private final CustomerRepository customerRepository;
	private final PaymentService paymentService;
	private final GatewayService gatewayService;
	private final MessagingService messagingService;

	private final Logger logger = LoggerFactory.getLogger(CustomerService.class);
	
	public CustomerService(CustomerRepository customerRepository,
                           PaymentService paymentApiService, GatewayService gatewayService,
                           @Lazy MessagingService messagingService) {
		
		this.customerRepository = customerRepository;
		this.paymentService = paymentApiService;
        this.gatewayService = gatewayService;
        this.messagingService = messagingService;
	}

	private Mono<Void> anonymiseCustomer(Customer customer) {
		return Mono.fromRunnable(() -> {
			customer.setEmail("anonymised_" + customer.getId() + "@glamoor.com");
			customer.setFirstName("Anonymous");
			customer.setLastName("Customer");
			customer.setProfilePicture(null);
			customer.setPhone(null);
			customer.setDefaultPaymentMethod(null);
			customer.setDefaultAddress(null);
			customer.setRecentRecentSearches(List.of());
			customer.setSettings(null);
			customer.setLastActive(LocalDateTime.now());
			customer.setStatus(CustomerStatus.ANONYMISED);
		});
	}

	private Mono<Customer> generateNewCustomer(String uid) {
		return Mono.defer(() -> {
			Customer newCustomer = new Customer();
			newCustomer.setUid(uid);
			newCustomer.setAnonymous(true);
			return customerRepository.save(newCustomer);
		});
	}

	public Mono<Customer> getCustomerByUidOrCreateCustomerByUid(String uid, boolean checkIn) {
		return customerRepository.findByUid(uid)
				.switchIfEmpty(generateNewCustomer(uid))
				.flatMap(customer -> {
					if (checkIn) {
						customer.setLastActive(LocalDateTime.now());
						return customerRepository.save(customer);
					}
					return Mono.just(customer);
				})
				.doOnError(e -> System.err.println("Error: " + e.getMessage()));
	}

	public Mono<Customer> getCustomerById(String customerId) {
		return customerRepository.findById(customerId)
				.switchIfEmpty(Mono.error(new EntityNotFoundException(customerId, EntityType.CUSTOMER)));
	}

	public Mono<Customer> saveCustomer(CustomerRequest request) {
		return customerRepository.findByUid(request.getUid())
				.flatMap(existingCustomer -> {
					if (!existingCustomer.getEmail().equals(request.getEmail())) {
						return customerRepository.findByEmail(request.getEmail())
								.flatMap(existingWithEmail ->
										Mono.<Customer>error(new IllegalArgumentException(
												"The email, " + request.getEmail() +
														" is already attached to another customer account."))
								)
								.switchIfEmpty(Mono.defer(() ->
										customerRepository.save(updateFields(request, existingCustomer))
								));
					}
					return customerRepository.save(updateFields(request, existingCustomer));
				})
				.switchIfEmpty(Mono.defer(() ->
						customerRepository.save(CustomerMapper.toCustomer(request))
								.doOnNext(this::sendNewCustomerMessages)
				));
	}

	private void sendNewCustomerMessages(Customer customer) {

		messagingService.sendMessage(MessagingService.BOOKING_EXCHANGE,
				MessagingService.BOOKINGS_CUSTOMERS_NEW_ROUTING_KEY,
				GlamoorJsonMapper.toJson(CustomerMapper.toCustomerRequestToBookings(customer)));
		messagingService.sendMessage(MessagingService.NOTIFICATION_EXCHANGE,
				MessagingService.NOTIFICATIONS_USERS_NEW_ROUTING_KEY,
				GlamoorJsonMapper.toJson(CustomerMapper.toCustomerRequestToNotifications(customer)));
	}

	private Customer updateFields(CustomerRequest customerRequest, Customer customer) {

		if (customerRequest.getFirstName() != null && !customerRequest.getFirstName().isEmpty()) {
			customer.setFirstName(customerRequest.getFirstName());
		}

		if (customerRequest.getLastName() != null && !customerRequest.getLastName().isEmpty()) {
			customer.setLastName(customerRequest.getLastName());
		}

		if (customerRequest.getEmail() != null && !customerRequest.getEmail().isEmpty()) {
			customer.setEmail(customerRequest.getEmail());
		}

		if (customerRequest.getPhone() != null) {
			customer.setPhone(CustomerMapper.toPhone(customerRequest.getPhone()));
		}

		customer.setAnonymous(false);

		return customer;
	}
	
	public Mono<Customer> addPaymentCustomerId(Customer customer) {
		try {
			return paymentService.getCustomerId(customer.getUid())
					.flatMap(paymentCustomerId -> {
						customer.setPaymentCustomerId(paymentCustomerId);
						return customerRepository.save(customer);
					});
		} catch (Exception e) {
			return Mono.just(customer);
		}
	}

	public Mono<String> getAccountProviderByEmail(String email) {
		return customerRepository.findByEmail(email)
				.flatMap(customer -> {
					if (customer.getStatus() == CustomerStatus.ACTIVE) {
						return Mono.justOrEmpty(customer.getAccountProvider());
					}
					return Mono.empty();
				});
	}


	public Mono<Void> setDefaultAddress(String customerId, String addressId) {
		return customerRepository.findById(customerId)
				.switchIfEmpty(Mono.error(new EntityNotFoundException(customerId, EntityType.CUSTOMER)))
				.flatMap(customer -> {
					customer.setDefaultAddress(addressId);
					return customerRepository.save(customer);
				})
				.then();
	}

	public Mono<UpdateCustomerResponse> updateCustomerDetails(String customerId, Customer update) {
		return customerRepository.findById(customerId)
				.switchIfEmpty(Mono.error(new EntityNotFoundException(customerId, EntityType.CUSTOMER)))
				.flatMap(existingCustomer -> {
					// 1. Determine what needs updating
					boolean shouldUpdateEmail = shouldUpdateEmail(existingCustomer, update);
					boolean updateNotifications = shouldUpdateNotificationsService(existingCustomer, update);
					boolean updateBookings = shouldUpdateBookingsService(existingCustomer, update);

					// 2. Prepare email update flow with token generation
					Mono<String> emailUpdateFlow = shouldUpdateEmail
							? gatewayService.updateUserEmail(existingCustomer.getId(), update.getEmail())
							: Mono.just("");

					// 3. Apply field updates
					updateCustomerFields(existingCustomer, update);

					// 4. Execute core flow
					return emailUpdateFlow
							.flatMap(token -> customerRepository.save(existingCustomer)
									.flatMap(updatedCustomer -> Mono.just(new UpdateCustomerResponse(
											CustomerMapper.toCustomerResponse(updatedCustomer),
											token)
									))
							)
							// 5. Execute side-effects
							.doOnSuccess(response -> {
								if (updateNotifications) {
									Mono.fromRunnable(() -> updateNotificationsService(existingCustomer))
											.subscribeOn(Schedulers.boundedElastic())
											.subscribe();
								}
								if (updateBookings) {
									Mono.fromRunnable(() -> updateBookingService(existingCustomer))
											.subscribeOn(Schedulers.boundedElastic())
											.subscribe();
								}
							});
				});
	}


	private boolean shouldUpdateEmail(Customer existing, Customer update) {
		return update.getEmail() != null
				&& !update.getEmail().isEmpty()
				&& !existing.getEmail().equals(update.getEmail());
	}

	private void updateCustomerFields(Customer target, Customer source) {

		if (source.getFirstName() != null && !source.getFirstName().isEmpty())
			target.setFirstName(source.getFirstName());

		if (source.getLastName() != null && !source.getLastName().isEmpty())
			target.setLastName(source.getLastName());

		if (source.getEmail() != null && !source.getEmail().isEmpty())
			target.setEmail(source.getEmail());

		Optional.ofNullable(source.getPhone())
				.ifPresent(target::setPhone);
		Optional.ofNullable(source.getLastUsedPaymentCard())
				.ifPresent(target::setLastUsedPaymentCard);
		Optional.ofNullable(source.getSettings())
				.ifPresent(target::setSettings);
	}


	public Mono<Void> setCustomerDefaultAddress(String customerId, String addressId) {
		return customerRepository.findById(customerId)
				.switchIfEmpty(Mono.error(new EntityNotFoundException(customerId, EntityType.CUSTOMER)))
				.flatMap(customer -> {
					customer.setDefaultAddress(addressId);
					return customerRepository.save(customer);
				})
				.then();
	}

	public Mono<Void> setCustomerDefaultPaymentMethod(DefaultPaymentMethodResponseFromPayments response) {
		return customerRepository.findByPaymentCustomerId(response.getPaymentsCustomerId())
				.switchIfEmpty(Mono.error(new EntityNotFoundException(
						response.getPaymentsCustomerId(),
						EntityType.CUSTOMER
				)))
				.flatMap(customer -> {
					customer.setDefaultPaymentMethod(response.getPaymentMethodId());
					return customerRepository.save(customer);
				})
				.then();
	}

	private boolean shouldUpdateNotificationsService(Customer existing, Customer update) {
		return (update.getPhone() != null && existing.getPhone() != update.getPhone()) ||
				(update.getEmail() != null && !update.getEmail().isEmpty() && !existing.getEmail().equals(update.getEmail()));
	}

	private boolean shouldUpdateBookingsService(Customer existing, Customer update) {
		return (update.getPhone() != null && existing.getPhone() != update.getPhone()) ||
				(update.getEmail() != null && !update.getEmail().isEmpty() && !existing.getEmail().equals(update.getEmail())) ||
				(update.getFirstName() != null && !update.getFirstName().isEmpty() && !existing.getFirstName().equals(update.getFirstName())) ||
				(update.getLastName() != null && !update.getLastName().isEmpty() && !existing.getLastName().equals(update.getLastName())) ||
				(update.getPaymentCustomerId() != null && !update.getPaymentCustomerId().isEmpty() && !existing.getPaymentCustomerId().equals(update.getPaymentCustomerId()));
	}

	private void updateNotificationsService(Customer customer) {
		messagingService.sendMessage(MessagingService.NOTIFICATION_EXCHANGE,
					MessagingService.NOTIFICATIONS_USERS_NEW_ROUTING_KEY,
					GlamoorJsonMapper.toJson(CustomerMapper.toCustomerRequestToNotifications(customer)));
	}

	private void updateBookingService(Customer customer) {
		messagingService.sendMessage(MessagingService.BOOKING_EXCHANGE,
					MessagingService.BOOKINGS_CUSTOMERS_UPDATE_ROUTING_KEY,
					GlamoorJsonMapper.toJson(CustomerMapper.toCustomerRequestToBookings(customer)));
	}

	public Mono<Void> updateCustomerProfilePicture(String customerId, String fileName) {
		return customerRepository.findById(customerId)
				.switchIfEmpty(Mono.error(new EntityNotFoundException(customerId, EntityType.CUSTOMER)))
				.flatMap(customer -> {
					customer.setProfilePicture(fileName);
					return customerRepository.save(customer).then();
				});
	}
	
	public Mono<Void> anonymiseCustomer(String customerId) {
		return customerRepository.findById(customerId)
				.switchIfEmpty(Mono.error(new EntityNotFoundException(customerId, EntityType.CUSTOMER)))
				.flatMap(customer_ -> {
					if (CustomerStatus.ANONYMISED == customer_.getStatus()) {
						return Mono.error(new IllegalArgumentException("Customer is already deleted."));
					}
					return anonymiseCustomer(customer_)
							.then(customerRepository.save(customer_))
							.doOnNext( customer -> Mono.fromRunnable(() -> {
                                    messagingService.sendMessage(MessagingService.NOTIFICATION_EXCHANGE,
                                            MessagingService.NOTIFICATIONS_USERS_DELETE_ROUTING_KEY, customer.getId());
                                    messagingService.sendMessage(MessagingService.PAYMENT_EXCHANGE,
                                            MessagingService.PAYMENTS_USERS_DELETE_ROUTING_KEY, customer.getPaymentCustomerId());

                                    messagingService.sendMessage(MessagingService.REVIEW_EXCHANGE,
                                            MessagingService.REVIEWS_CUSTOMERS_DELETE_ROUTING_KEY, customer.getId());
                                    messagingService.sendMessage(MessagingService.BOOKING_EXCHANGE,
                                            MessagingService.BOOKINGS_CUSTOMERS_ANONYMISE_ROUTING_KEY, customer.getId());
                                })
                                .subscribeOn(Schedulers.boundedElastic())
                                .subscribe())
							.then();
				});

	}

	public Mono<Boolean> validateRequestSender(String id1, String id2) {
		return Mono.fromSupplier(() -> {
			if (id1 == null || !id1.equals(id2)) {
				throw new IllegalArgumentException("Unauthorized access.");
			}
			return true;
		});
	}
	
}
