package uk.co.glamoor.customers.service.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.co.glamoor.customers.config.APIConfig;
import uk.co.glamoor.customers.exception.APIException;

import java.time.Duration;


@Service
public class PaymentService {

	private final APIConfig apiConfig;
	private final WebClient paymentServiceClient;
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

	public PaymentService(@Qualifier("paymentServiceClient") WebClient paymentServiceClient, APIConfig apiConfig) {
        this.apiConfig = apiConfig;
        this.paymentServiceClient = paymentServiceClient;
	}
	
	public Mono<String> getCustomerId(String uid) {
		return paymentServiceClient.post()
				.uri(uriBuilder -> uriBuilder
						.path("/customers")
						.queryParam("uid", uid)
						.build())
				.retrieve()
				.onStatus(HttpStatusCode::isError, response ->
						response.bodyToMono(String.class)
								.flatMap(errorBody ->
										Mono.error(new APIException(errorBody, 3)))
				)
				.bodyToMono(String.class)
				.timeout(Duration.ofSeconds(Integer.parseInt(
						apiConfig.getPaymentsApi().getRequestTimeout())))
				.doOnError(WebClientResponseException.class, ex -> {
					throw new APIException("Payments API Request Error: "
							+ ex.getStatusCode() + " " + ex.getResponseBodyAsString(), 1);
				});
	}
	
	
	public boolean deleteCustomer(String customerId) {
	    try {
	        paymentServiceClient.delete()
	                .uri(uriBuilder -> uriBuilder
	                        .path("/customers")
	                        .queryParam("id", customerId)
	                        .build())
	                .retrieve()
	                .toBodilessEntity()
	                .block();
	        return true;
	    } catch (WebClientResponseException e) {
	        if (e.getStatusCode().is4xxClientError() || e.getStatusCode().is5xxServerError()) {
	            return false;
	        }
	        throw new RuntimeException("Error calling API: " + e.getStatusCode(), e); // Re-throw unexpected errors
	    }
	}

}
