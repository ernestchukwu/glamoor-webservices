package uk.co.glamoor.posts.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Data
@Configuration
@ConfigurationProperties(prefix = "posts-app")
@RefreshScope
public class PostsConfig {
    private int postsRequestBatchSize;
    private int postsRequestBatchSizeForHomeView;
}
