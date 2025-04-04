package uk.co.glamoor.reviews.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "reviews-app")
@RefreshScope
public class ReviewsConfig {
	
	private int reviewRequestBatchSize;
	private int reviewRepliesRequestBatchSize;

}
