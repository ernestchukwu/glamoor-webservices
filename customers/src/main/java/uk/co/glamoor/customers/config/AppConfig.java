package uk.co.glamoor.customers.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
@RefreshScope
public class AppConfig {

    private Images images;

    @Data
    public static class Images {
        private String defaultFormat;
        private Directories directories;
    }

    @Data
    public static class Directories {
        private String userProfilePictures;
    }

}
