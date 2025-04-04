package uk.co.glamoor.stylists;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import uk.co.glamoor.stylists.config.AppConfig;
import uk.co.glamoor.stylists.config.StylistsConfig;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({AppConfig.class, StylistsConfig.class})
public class StylistsApplication {

	public static void main(String[] args) {
		SpringApplication.run(StylistsApplication.class, args);
	}

}
