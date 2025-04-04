package uk.co.glamoor.bookings.config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import lombok.Setter;
import org.springframework.amqp.core.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

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

    public static final String DELAYED_EXCHANGE = "reservationDelayedExchange";
    public static final String DELAYED_QUEUE = "reservationDelayQueue";
    public static final String ROUTING_KEY = "reservation.cancel";

    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAYED_EXCHANGE, "x-delayed-message", true, false, args);
    }

    @Bean
    public Queue delayedQueue() {
        return QueueBuilder.durable(DELAYED_QUEUE).build();
    }

    @Bean
    public Binding delayedBinding(Queue delayedQueue, CustomExchange delayedExchange) {
        return BindingBuilder.bind(delayedQueue).to(delayedExchange).with(ROUTING_KEY).noargs();
    }
}
