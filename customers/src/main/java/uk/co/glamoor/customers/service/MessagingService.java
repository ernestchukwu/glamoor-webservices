package uk.co.glamoor.customers.service;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import jakarta.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;
import uk.co.glamoor.customers.mapper.GlamoorJsonMapper;
import uk.co.glamoor.customers.dto.messaging.response.DefaultPaymentMethodResponseFromPayments;

@Service
public class MessagingService {

	private static final String EXCHANGE_NAME = "customer-topic-exchange";
    private static final String QUEUE_NAME = "customer-queue";
    private static final String ROUTING_KEY_PATTERN = "customers.*";

    public static final String BOOKING_EXCHANGE = "booking-topic-exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification-topic-exchange";
    public static final String PAYMENT_EXCHANGE = "notification-topic-exchange";
    public static final String REVIEW_EXCHANGE = "review-topic-exchange";
    
    private final Logger logger = LoggerFactory.getLogger(MessagingService.class);

    public static final String CUSTOMERS_DEFAULT_PAYMENT_UPDATE_ROUTING_KEY = "customers.default_payment_update";

    // For notifications service
    public static final String NOTIFICATIONS_USERS_DELETE_ROUTING_KEY = "notifications.users_delete";
    public static final String NOTIFICATIONS_USERS_NEW_ROUTING_KEY = "notifications.users_new";

    // For notifications service
    public static final String PAYMENTS_USERS_DELETE_ROUTING_KEY = "payments.users_delete";
    
    // For bookings service
    public static final String BOOKINGS_CUSTOMERS_ANONYMISE_ROUTING_KEY = "bookings.customers_anonymise";
    public static final String BOOKINGS_CUSTOMERS_NEW_ROUTING_KEY = "bookings.customers_new";
    public static final String BOOKINGS_CUSTOMERS_UPDATE_ROUTING_KEY = "bookings.customers_update";

    // For review service
    public static final String REVIEWS_CUSTOMERS_DELETE_ROUTING_KEY = "reviews.customers_delete";
    
    
    

    private final Connection connection;
    private final CustomerService customerService;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();


    public MessagingService(Connection connection,
                            @Lazy CustomerService customerService) {
    	this.customerService = customerService;
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
        executor.submit(() -> {
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
            } catch (Exception e) {
                logger.error("Could not send message: " + message, e);
            }
        });
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

            switch(routingKey) {
                case CUSTOMERS_DEFAULT_PAYMENT_UPDATE_ROUTING_KEY:
                    DefaultPaymentMethodResponseFromPayments response =
                            GlamoorJsonMapper.fromJson(message, DefaultPaymentMethodResponseFromPayments.class);

                    customerService.setCustomerDefaultPaymentMethod(Objects.requireNonNull(response))
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe(
                                    null,
                                    error -> logger.error("Failed to process payment method update", error),
                                    () -> logger.debug("Successfully processed payment method update")
                            );
                    break;

            }
            
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        System.out.println("Started receiving messages from queue: " + QUEUE_NAME);
    }

    @PreDestroy
    public void shutdownExecutor() {
        executor.shutdown();
    }
    
}

