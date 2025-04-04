package uk.co.glamoor.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import uk.co.glamoor.payments.config.APIConfig;
import uk.co.glamoor.payments.config.PaymentsConfig;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({APIConfig.class, PaymentsConfig.class})
public class PaymentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentsApplication.class, args);
	}

}
