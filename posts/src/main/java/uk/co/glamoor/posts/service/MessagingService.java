package uk.co.glamoor.posts.service;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import jakarta.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {

	private static final String EXCHANGE_NAME = "post-topic-exchange";
    private static final String QUEUE_NAME = "post-queue";
    private static final String ROUTING_KEY_PATTERN = "posts.*";
    
    
    private final Logger logger = LoggerFactory.getLogger(MessagingService.class);
    
    public static final String POSTS_STYLISTS_DELETE_ROUTING_KEY = "posts.stylists_delete";
    public static final String POSTS_STYLISTS_UPDATE_ROUTING_KEY = "posts.stylists_update";

    private final Connection connection;
    
    private final PostService postService;

    public MessagingService(Connection connection,
                            PostService postService) {
    	
        this.connection = connection;
        this.postService = postService;
    }

    @PostConstruct
    public void initializeReceiver() {
        try {
            setupQueue();
            startReceiving();
        } catch (Exception e) {
            e.printStackTrace();
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
            channel.exchangeDeclare(exchangeName, "topic", true);
            channel.basicPublish(exchangeName, routingKey, properties, message.getBytes());
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
                       
            Map<String, Object> propertiesHeaders ;
            switch(routingKey) {
	            
	            case POSTS_STYLISTS_DELETE_ROUTING_KEY:
	            	postService.deleteStylist(message);
	            	break;
	            case POSTS_STYLISTS_UPDATE_ROUTING_KEY:
	            	propertiesHeaders = delivery.getProperties().getHeaders();
	            	postService.updateStylistAlias(message, (String) propertiesHeaders.get("alias"));
                    break;
            }
            
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        System.out.println("Started receiving messages from queue: " + QUEUE_NAME);
    }
    
}

