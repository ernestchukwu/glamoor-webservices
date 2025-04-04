package uk.co.glamoor.payments.service;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class MessagingService {

	private static final String EXCHANGE_NAME = "payment-topic-exchange";
    private static final String QUEUE_NAME = "payment-queue";
    private static final String ROUTING_KEY_PATTERN = "payments.*";
    
    
    public static final String CUSTOMER_EXCHANGE = "customer-topic-exchange";
    
    private final Logger logger = LoggerFactory.getLogger(MessagingService.class);

    public static final String CUSTOMERS_DEFAULT_PAYMENT_UPDATE_ROUTING_KEY = "customers.default_payment_update";

    private final Connection connection;

    public MessagingService(Connection connection) {
        this.connection = connection;
    }

    @PostConstruct
    public void initializeReceiver() {
        try {
            setupQueue();
            startReceiving();
        } catch (Exception e) {
            logger.error("There was a problem initializing message receiver", e);
        }
    }

    private void setupQueue() throws Exception {
        try (Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY_PATTERN);
            System.out.println("Queue setup completed with routing key pattern: " + ROUTING_KEY_PATTERN);
        }
    }
    
    public void sendMessage(String exchangeName, String routingKey, String message, Map<String, Object> headerProperties) {
    	
    	BasicProperties properties = null;
    	
    	if (headerProperties != null) {
    		properties = new BasicProperties.Builder()
    	            .headers(headerProperties)
    	            .build();
    	}
    	
        try (Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC, true);
            channel.basicPublish(exchangeName, routingKey, properties, message.getBytes());
            System.out.println("Exchange: " + exchangeName);
            System.out.println("Sent [" + routingKey + "]: " + message);
        } catch(Exception e) {
			logger.error("Could not send message: " + message, e);
		}
    }
    
    public void sendMessage(String exchangeName, String routingKey, String message) {
    	sendMessage(exchangeName, routingKey, message, null);
    }
    
    
    private void startReceiving() throws Exception {
        Channel channel = connection.createChannel();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            String routingKey = delivery.getEnvelope().getRoutingKey();
            
            Map<String, Object> propertiesHeaders;

        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        System.out.println("Started receiving messages from queue: " + QUEUE_NAME);
    }
    
}

