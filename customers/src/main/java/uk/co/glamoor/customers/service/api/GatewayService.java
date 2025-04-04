package uk.co.glamoor.customers.service.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.co.glamoor.customers.config.APIConfig;
import uk.co.glamoor.customers.exception.APIException;
import uk.co.glamoor.customers.mapper.CustomerMapper;
import uk.co.glamoor.customers.model.Customer;

import java.time.Duration;

@Service
public class GatewayService {

    private final APIConfig apiConfig;
    private final WebClient gatewayServiceClient;

    private final Logger logger = LoggerFactory.getLogger(GatewayService.class);

    public GatewayService(APIConfig apiConfig, WebClient gatewayServiceClient) {
        this.apiConfig = apiConfig;
        this.gatewayServiceClient = gatewayServiceClient;
    }

    public Mono<Void> addUser(Customer customer) {
        return gatewayServiceClient.post()
                .uri("/users")
                .bodyValue(CustomerMapper.toCustomerRequestToGateway(customer))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.createException()
                                .flatMap(ex -> Mono.error(new APIException(
                                        ex.getMessage(), ex, 3)))
                )
                .bodyToMono(Void.class)
                .timeout(Duration.ofSeconds(Integer.parseInt(apiConfig.getGatewayApi().getRequestTimeout())))
                .doOnError(WebClientResponseException.class, ex -> {
                    throw new APIException("Gateway API Request Error updating user email: "
                            + ex.getStatusCode() + " " + ex.getResponseBodyAsString(), 1);
                });
    }

    public Mono<String> updateUserEmail(String customerId, String email) {
        return gatewayServiceClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/" + customerId + "/update-email")
                        .queryParam("email", email)
                        .build()
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody ->
                                        Mono.error(new APIException(errorBody, 3)))
                )
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(Integer.parseInt(
                        apiConfig.getGatewayApi().getRequestTimeout())))
                .doOnError(WebClientResponseException.class, ex -> {
                    throw new APIException("Gateway API Request Error updating user email: "
                            + ex.getStatusCode() + " " + ex.getResponseBodyAsString(), 1);
                });
    }

    public Mono<Void> deleteUser(Customer customer) {
        return gatewayServiceClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/users")
                        .queryParam("uid", customer.getUid())
                        .queryParam("userId", customer.getId())
                        .build()
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody ->
                                        Mono.error(new APIException(errorBody, 3)))
                )
                .bodyToMono(Void.class)
                .timeout(Duration.ofSeconds(Integer.parseInt(
                        apiConfig.getGatewayApi().getRequestTimeout())))
                .doOnError(WebClientResponseException.class, ex -> {
                    throw new APIException("Gateway API Request Error deleting user: "
                            + ex.getStatusCode() + " " + ex.getResponseBodyAsString(), 1);
                })
                .then();
    }
}
