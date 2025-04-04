package uk.co.glamoor.posts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import uk.co.glamoor.posts.configuration.AppConfig;
import uk.co.glamoor.posts.configuration.PostsConfig;
import uk.co.glamoor.posts.configuration.RabbitMQConfig;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({AppConfig.class, PostsConfig.class, RabbitMQConfig.class})
public class PostsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostsApplication.class, args);
	}

}
