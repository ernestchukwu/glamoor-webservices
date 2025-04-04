package uk.co.glamoor.stylists.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
@RefreshScope
public class AppConfig {

	private int queryResultsMaxDistance;
	private double defaultLongitude;
	private double defaultLatitude;

}
