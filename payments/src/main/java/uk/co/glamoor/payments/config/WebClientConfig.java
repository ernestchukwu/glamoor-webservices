package uk.co.glamoor.payments.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Getter
public class WebClientConfig {

    private final APIConfig apiConfig;

    public WebClientConfig(APIConfig apiConfig) {
        this.apiConfig = apiConfig;
    }

    @Bean
    @Qualifier("bookingsWebClient")
    public WebClient stylistsWebClient() {
        return WebClient.builder().baseUrl(this.apiConfig.getBookingsApi().getBaseUrl()).build();
    }

}

