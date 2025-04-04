package uk.co.glamoor.customers.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;


@ConfigurationProperties(prefix = "api")
@Data
@RefreshScope
public class APIConfig {

    private API paymentsApi;
    private API gatewayApi;


    @Data
    public static class API {
        private String baseUrl;
        private String requestTimeout;
    }

}
