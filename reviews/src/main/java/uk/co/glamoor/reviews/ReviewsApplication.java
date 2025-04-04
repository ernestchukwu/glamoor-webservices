package uk.co.glamoor.reviews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import uk.co.glamoor.reviews.config.APIConfig;
import uk.co.glamoor.reviews.config.RabbitMQConfig;
import uk.co.glamoor.reviews.config.ReviewsConfig;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({APIConfig.class, ReviewsConfig.class, RabbitMQConfig.class})
public class ReviewsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReviewsApplication.class, args);
	}

}
