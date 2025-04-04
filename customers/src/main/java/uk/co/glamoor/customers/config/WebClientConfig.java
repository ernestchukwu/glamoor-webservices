package uk.co.glamoor.customers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private final APIConfig apiConfig;

    public WebClientConfig(APIConfig apiConfig) {
        this.apiConfig = apiConfig;
    }

    @Bean
    public WebClient paymentServiceClient() {
        return WebClient.builder().baseUrl(this.apiConfig.getPaymentsApi().getBaseUrl()).build();
    }

    @Bean
    public WebClient gatewayServiceClient() {
        return WebClient.builder().baseUrl(this.apiConfig.getGatewayApi().getBaseUrl()).build();
    }
}
