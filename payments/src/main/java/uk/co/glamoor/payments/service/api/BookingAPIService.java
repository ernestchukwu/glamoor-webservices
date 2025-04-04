package uk.co.glamoor.payments.service.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.co.glamoor.payments.config.APIConfig;
import uk.co.glamoor.payments.exception.PostPaymentConfirmationException;
import uk.co.glamoor.payments.model.Payment;
import uk.co.glamoor.payments.mapper.PaymentMapper;

import java.time.Duration;

@Service
public class BookingAPIService {

    private final WebClient webClient;
    private final APIConfig apiConfig;

    public BookingAPIService(@Qualifier("bookingsWebClient") WebClient webClient, APIConfig apiConfig) {
        this.apiConfig = apiConfig;
        this.webClient = webClient;
    }

    public Mono<Void> addPayment(Payment payment) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/" + payment.getBookingId() + "/payments")
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(PaymentMapper.toBookingPayment(payment))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody ->
                                        Mono.error(new PostPaymentConfirmationException("Error calling bookings API: " + errorBody)))
                )
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(Integer.parseInt(
                        apiConfig.getBookingsApi().getRequestTimeout())))
                .onErrorMap(WebClientResponseException.class, ex ->
                        new PostPaymentConfirmationException("Error calling bookings API: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString())
                ).then();
    }
}
