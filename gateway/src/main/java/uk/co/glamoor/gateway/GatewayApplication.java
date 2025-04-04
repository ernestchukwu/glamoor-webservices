package uk.co.glamoor.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;
import uk.co.glamoor.gateway.config.AppConfig;
import uk.co.glamoor.gateway.config.bookings.BookingsConfig;
import uk.co.glamoor.gateway.config.stylists.StylistsConfig;

@SpringBootApplication
@EnableConfigurationProperties({AppConfig.class, BookingsConfig.class, StylistsConfig.class})
@EnableRetry
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
