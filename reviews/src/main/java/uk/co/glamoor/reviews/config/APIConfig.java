package uk.co.glamoor.reviews.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "api")
@Data
@RefreshScope
public class APIConfig {
    private API bookingsApi;

    @Data
    @Getter
    @Setter
    public static class API {
        private String baseUrl;
        private String requestTimeout;
    }

}
