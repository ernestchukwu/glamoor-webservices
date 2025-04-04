package uk.co.glamoor.customers.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
@Setter
@RefreshScope
public class RabbitMQConfig {

    private String host;
    private int port;
    private String username;
    private String password;

    @Bean
    public Connection rabbitMQConnection() throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory.newConnection();
    }
}
