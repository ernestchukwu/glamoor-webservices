package uk.co.glamoor.bookings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import uk.co.glamoor.bookings.config.APIConfig;
import uk.co.glamoor.bookings.config.AppConfig;
import uk.co.glamoor.bookings.config.RabbitMQConfig;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({APIConfig.class, AppConfig.class, RabbitMQConfig.class})
public class BookingsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingsApplication.class, args);
	}

}
