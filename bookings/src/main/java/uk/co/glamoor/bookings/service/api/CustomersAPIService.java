package uk.co.glamoor.bookings.service.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.co.glamoor.bookings.config.APIConfig;
import uk.co.glamoor.bookings.dto.response.CustomerResponse;
import uk.co.glamoor.bookings.mapper.CustomerMapper;
import uk.co.glamoor.bookings.model.Customer;

import java.time.Duration;


@Service
public class CustomersAPIService {

	private final WebClient webClient;
	private final APIConfig apiConfig;

	public CustomersAPIService(@Qualifier("customersWebClient") WebClient webClient, APIConfig apiConfig) {
		this.apiConfig = apiConfig;
        this.webClient = webClient;
	}

	public Mono<Customer> getCustomer(String customerId) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/"+customerId+"/bookings-service")
						.build())
				.retrieve()
				.onStatus(HttpStatusCode::isError, response ->
						response.bodyToMono(String.class)
								.flatMap(errorBody ->
										Mono.error(new RuntimeException("Error calling customers API: " + errorBody)))
				)
				.bodyToMono(CustomerResponse.class)
				.timeout(Duration.ofSeconds(Integer.parseInt(
						apiConfig.getCustomersApi().getRequestTimeout())))
				.onErrorMap(WebClientResponseException.class, ex ->
						new RuntimeException("Error calling customers API: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString(), ex)
				)
				.map(CustomerMapper::toCustomer);

	}


}
