package uk.co.glamoor.bookings.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebClientConfig {

    private final APIConfig apiConfig;

    public WebClientConfig(APIConfig apiConfig) {
        this.apiConfig = apiConfig;
    }

    @Bean
    @Qualifier("stylistsWebClient")
    public WebClient stylistsWebClient() {
        return WebClient.builder().baseUrl(this.apiConfig.getStylistsApi().getBaseUrl()).build();
    }

    @Bean
    @Qualifier("customersWebClient")
    public WebClient customersWebClient() {
        return WebClient.builder().baseUrl(this.apiConfig.getCustomersApi().getBaseUrl()).build();
    }

    @Bean
    @Qualifier("paymentsWebClient")
    public WebClient paymentsWebClient() {
        return WebClient.builder().baseUrl(this.apiConfig.getPaymentsApi().getBaseUrl()).build();
    }
}

