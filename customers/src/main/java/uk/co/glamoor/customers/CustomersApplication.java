package uk.co.glamoor.customers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import uk.co.glamoor.customers.config.APIConfig;
import uk.co.glamoor.customers.config.AppConfig;
import uk.co.glamoor.customers.config.RabbitMQConfig;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({APIConfig.class, RabbitMQConfig.class, AppConfig.class})
public class CustomersApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomersApplication.class, args);
	}

}
