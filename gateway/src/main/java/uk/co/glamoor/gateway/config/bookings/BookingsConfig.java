package uk.co.glamoor.gateway.config.bookings;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Data
@Component
@ConfigurationProperties(prefix = "bookings-app")
@RefreshScope
public class BookingsConfig {
    private int bookingsRequestBatchSize;
    private int messagesRequestBatchSize;
}
