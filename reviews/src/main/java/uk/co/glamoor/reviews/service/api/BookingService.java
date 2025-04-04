package uk.co.glamoor.reviews.service.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.co.glamoor.reviews.config.APIConfig;


@Service
public class BookingService {

	private final WebClient webClient;
	
	private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

	public BookingService(WebClient.Builder webClientBuilder, APIConfig apiConfig) {
		this.webClient = webClientBuilder.baseUrl(apiConfig.getBookingsApi().getBaseUrl()).build();
	}
	
	public Mono<Boolean> isBookingCustomer(String customerId, String bookingId) {
		try {
			return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/is-booking-customer")
                            .queryParam("bookingId", bookingId)
                            .queryParam("customerId", customerId)
                            .build())
                    .retrieve()
                    .bodyToMono(Boolean.class);
		} catch (WebClientResponseException e) {
			logger.error("API error: {}", e.getResponseBodyAsString());
			throw new RuntimeException("Error calling booking API: " + e.getStatusCode(), e);
		}
	}
}
