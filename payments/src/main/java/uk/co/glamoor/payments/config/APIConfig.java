package uk.co.glamoor.payments.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@ConfigurationProperties(prefix = "api")
@Data
@RefreshScope
public class APIConfig {
    private API bookingsApi;


    @Data
    public static class API {
        private String baseUrl;
        private String requestTimeout;
    }

}
