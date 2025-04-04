package uk.co.glamoor.notifications.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "notifications-app")
@RefreshScope
public class AppConfig {

    private int notificationRequestBatchSize;
    private String bookingsNotificationSenderEmail;

}

