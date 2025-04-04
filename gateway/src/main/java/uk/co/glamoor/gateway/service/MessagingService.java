package uk.co.glamoor.gateway.service;

import java.nio.charset.StandardCharsets;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {

	public static final String EXCHANGE_NAME = "auth-topic-exchange";
	public static final String QUEUE_NAME = "auth-queue";

	private static final String AUTH_USERS_DELETE_ROUTING_KEY = "auth.users_delete";

	private final Logger logger = LoggerFactory.getLogger(MessagingService.class);

	public static final String ROUTING_KEY_PATTERN = "auth.*";

	private final FirebaseService firebaseService;
	private final Connection connection;

	public MessagingService(Connection connection, FirebaseService firebaseService) {
		this.connection = connection;
		this.firebaseService = firebaseService;
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

	private void startReceiving() throws Exception {
		Channel channel = connection.createChannel();

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
			String routingKey = delivery.getEnvelope().getRoutingKey();

            if (routingKey.equals(AUTH_USERS_DELETE_ROUTING_KEY)) {
                firebaseService.deleteUser(message);
            }

		};

		channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
		System.out.println("Started receiving messages from queue: " + QUEUE_NAME);
	}
}
