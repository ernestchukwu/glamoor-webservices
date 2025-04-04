package uk.co.glamoor.payments.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Data
@Configuration
@ConfigurationProperties(prefix = "payments-app")
@RefreshScope
public class PaymentsConfig {
    private String stripeSecretKey;
}
