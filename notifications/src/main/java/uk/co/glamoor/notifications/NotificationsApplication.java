package uk.co.glamoor.notifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import uk.co.glamoor.notifications.config.AppConfig;
import uk.co.glamoor.notifications.config.RabbitMQConfig;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({AppConfig.class, RabbitMQConfig.class})
public class NotificationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationsApplication.class, args);
	}

}
