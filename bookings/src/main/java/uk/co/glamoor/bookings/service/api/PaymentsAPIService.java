package uk.co.glamoor.bookings.service.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.co.glamoor.bookings.config.APIConfig;
import uk.co.glamoor.bookings.dto.request.PaymentIntentRequest;
import uk.co.glamoor.bookings.dto.response.PaymentIntentResponse;

import java.time.Duration;

@Service
public class PaymentsAPIService {

    private final WebClient webClient;
    private final APIConfig apiConfig;

    public PaymentsAPIService(@Qualifier("paymentsWebClient") WebClient webClient, APIConfig apiConfig) {
        this.apiConfig = apiConfig;
        this.webClient = webClient;
    }

    public Mono<PaymentIntentResponse> createPaymentIntent(PaymentIntentRequest bookingRequest) {
        return webClient.post()
                .uri("/payment-intent")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookingRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody ->
                                        Mono.error(new RuntimeException("Error creating payment intent: " + errorBody)))
                )
                .bodyToMono(PaymentIntentResponse.class)
                .timeout(Duration.ofSeconds(Integer.parseInt(
                        apiConfig.getPaymentsApi().getRequestTimeout())))
                .onErrorMap(WebClientResponseException.class, ex ->
                        new RuntimeException("Error creating payment intent: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString(), ex)
                );
    }
}
