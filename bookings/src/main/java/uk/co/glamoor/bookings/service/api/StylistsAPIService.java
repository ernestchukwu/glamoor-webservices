package uk.co.glamoor.bookings.service.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.co.glamoor.bookings.config.APIConfig;
import uk.co.glamoor.bookings.dto.response.HomeServiceSpecificationResponse;
import uk.co.glamoor.bookings.dto.response.ServiceProviderResponse;
import uk.co.glamoor.bookings.dto.response.StylistResponse;
import uk.co.glamoor.bookings.dto.response.StylistServiceSpecificationResponse;
import uk.co.glamoor.bookings.mapper.*;
import uk.co.glamoor.bookings.model.*;

import java.time.Duration;

@Service
public class StylistsAPIService {

	private final WebClient webClient;
	private final APIConfig apiConfig;

	public StylistsAPIService(@Qualifier("stylistsWebClient") WebClient webClient, APIConfig apiConfig) {
		this.apiConfig = apiConfig;
		this.webClient = webClient;
	}

	public Mono<Stylist> getStylist(String stylistId) {
			return webClient.get()
					.uri(uriBuilder -> uriBuilder
							.path("/"+stylistId+"/bookings-service")
							.build())
					.retrieve()
					.onStatus(HttpStatusCode::isError, response ->
						response.bodyToMono(String.class)
								.flatMap(errorBody ->
										Mono.error(new RuntimeException("Error calling stylists API: " + errorBody)))
					)
					.bodyToMono(StylistResponse.class)
					.timeout(Duration.ofSeconds(Integer.parseInt(
							apiConfig.getStylistsApi().getRequestTimeout())))
					.onErrorMap(WebClientResponseException.class, ex ->
						new RuntimeException("Error calling stylists API: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString(), ex)
					)
					.map(StylistMapper::toStylist);

	}

	public Mono<StylistServiceSpecification> getStylistServiceSpecification(String stylistId, String serviceSpecificationId) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/"+stylistId+"/service-specifications/" + serviceSpecificationId+"/bookings-service")
						.build())
				.retrieve()
				.onStatus(HttpStatusCode::isError, response ->
						response.bodyToMono(String.class)
								.flatMap(errorBody ->
										Mono.error(new RuntimeException("Error calling stylists API: " + errorBody)))
				)
				.bodyToMono(StylistServiceSpecificationResponse.class)
				.timeout(Duration.ofSeconds(Integer.parseInt(
						apiConfig.getStylistsApi().getRequestTimeout())))
				.onErrorMap(WebClientResponseException.class, ex ->
						new RuntimeException("Error calling stylists API: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString(), ex)
				)
				.map(StylistServiceSpecificationMapper::toStylistServiceSpecification);

	}

	public Mono<HomeServiceSpecification> getStylistHomeServiceSpecification(String stylistId, String homeServiceSpecificationId) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/"+stylistId+"/home-service-specifications/" + homeServiceSpecificationId+"/bookings-service")
						.build())
				.retrieve()
				.onStatus(HttpStatusCode::isError, response ->
						response.bodyToMono(String.class)
								.flatMap(errorBody ->
										Mono.error(new RuntimeException("Error calling stylists API: " + errorBody)))
				)
				.bodyToMono(HomeServiceSpecificationResponse.class)
				.timeout(Duration.ofSeconds(Integer.parseInt(
						apiConfig.getStylistsApi().getRequestTimeout())))
				.onErrorMap(WebClientResponseException.class, ex ->
						new RuntimeException("Error calling stylists API: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString(), ex)
				)
				.map(HomeServiceSpecificationMapper::toHomeServiceSpecification);

	}

	public Mono<ServiceProvider> getStylistServiceProvider(String stylistId, String serviceProviderId) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/"+stylistId+"/service-providers/" + serviceProviderId+"/bookings-service")
						.build())
				.retrieve()
				.onStatus(HttpStatusCode::isError, response ->
						response.bodyToMono(String.class)
								.flatMap(errorBody ->
										Mono.error(new RuntimeException("Error calling stylists API: " + errorBody)))
				)
				.bodyToMono(ServiceProviderResponse.class)
				.timeout(Duration.ofSeconds(Integer.parseInt(
						apiConfig.getStylistsApi().getRequestTimeout())))
				.onErrorMap(WebClientResponseException.class, ex ->
						new RuntimeException("Error calling stylists API: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString(), ex)
				)
				.map(ServiceProviderMapper::toServiceProvider);

	}
}
