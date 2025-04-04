package uk.co.glamoor.stylists.service;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import jakarta.annotation.PostConstruct;
import uk.co.glamoor.stylists.mapper.GlamoorJsonMapper;
import uk.co.glamoor.stylists.model.GlamoorService;
import uk.co.glamoor.stylists.model.availability_request.DailyAvailabilityRequest;
import uk.co.glamoor.stylists.model.availability_request.MonthlyAvailabilityRequest;
import uk.co.glamoor.stylists.model.availability_request.TimeSlot;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {

	private static final String EXCHANGE_NAME = "stylist-topic-exchange";
    private static final String QUEUE_NAME = "stylist-queue";
    private static final String ROUTING_KEY_PATTERN = "stylists.*";
    
    
    public static final String BOOKING_EXCHANGE = "booking-topic-exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification-topic-exchange";
    public static final String POST_EXCHANGE = "post-topic-exchange";
    public static final String REVIEW_EXCHANGE = "review-topic-exchange";
    
    private final Logger logger = LoggerFactory.getLogger(MessagingService.class);
    
    public static final String STYLISTS_AVAILABILITIES_MONTHLY_ADD_ROUTING_KEY = "stylists.availabilities_monthly_add";
    public static final String STYLISTS_AVAILABILITIES_DAILY_ADD_ROUTING_KEY = "stylists.availabilities_daily_add";
    public static final String STYLISTS_AVAILABILITIES_MONTHLY_DELETE_ROUTING_KEY = "stylists.availabilities_monthly_delete";
    public static final String STYLISTS_AVAILABILITIES_DAILY_DELETE_ROUTING_KEY = "stylists.availabilities_daily_delete";
    public static final String STYLISTS_AVAILABILITIES_BOOK_ROUTING_KEY = "stylists.availabilities_book";
    public static final String STYLISTS_AVAILABILITIES_UNBOOK_ROUTING_KEY = "stylists.availabilities_unbook";
    public static final String STYLISTS_SERVICES_UPDATE_ROUTING_KEY = "stylists.services_update";
    
    // For bookings service
    public static final String BOOKINGS_STYLISTS_NEW_ROUTING_KEY = "bookings.stylists_new";
    public static final String BOOKINGS_STYLISTS_UPDATE_ROUTING_KEY = "bookings.stylists_update";
    public static final String BOOKINGS_STYLISTS_ANONYMISE_ROUTING_KEY = "bookings.stylists_anonymise";
    public static final String BOOKINGS_SERVICES_UPDATE_ROUTING_KEY = "bookings.services_update";

    public static final String BOOKINGS_STYLISTS_SERVICE_SPECIFICATION_NEW_ROUTING_KEY = "bookings.stylists_service-specifications_new";
    public static final String BOOKINGS_STYLISTS_SERVICE_SPECIFICATION_UPDATE_ROUTING_KEY = "bookings.stylists_service-specifications_update";
    public static final String BOOKINGS_STYLISTS_SERVICE_SPECIFICATION_DELETE_ROUTING_KEY = "bookings.stylists_service-specifications_delete";
    
    public static final String BOOKINGS_STYLISTS_HOME_SERVICE_SPECIFICATION_NEW_ROUTING_KEY = "bookings.stylists_home-service-specifications_new";
    public static final String BOOKINGS_STYLISTS_HOME_SERVICE_SPECIFICATION_UPDATE_ROUTING_KEY = "bookings.stylists_home-service-specifications_update";
    public static final String BOOKINGS_STYLISTS_HOME_SERVICE_SPECIFICATION_DELETE_ROUTING_KEY = "bookings.stylists_home-service-specifications_delete";
    
    public static final String BOOKINGS_STYLISTS_SERVICE_PROVIDER_NEW_ROUTING_KEY = "bookings.stylists_service-providers_new";
    public static final String BOOKINGS_STYLISTS_SERVICE_PROVIDER_UPDATE_ROUTING_KEY = "bookings.stylists_service-providers_update";
    public static final String BOOKINGS_STYLISTS_SERVICE_PROVIDER_DELETE_ROUTING_KEY = "bookings.stylists_service-providers_delete";
    
    
    // For notifications service
    public static final String NOTIFICATIONS_USERS_NEW_ROUTING_KEY = "notifications.users_new";
    public static final String NOTIFICATIONS_USERS_UPDATE_ROUTING_KEY = "notifications.users_update";
    public static final String NOTIFICATIONS_USERS_DELETE_ROUTING_KEY = "notifications.users_delete";
    
    // For posts service
    public static final String POSTS_STYLISTS_UPDATE_ROUTING_KEY = "posts.stylists_update";
    public static final String POSTS_STYLISTS_DELETE_ROUTING_KEY = "posts.stylists_delete";
    
    // For posts service
    public static final String REVIEWS_STYLISTS_DELETE_ROUTING_KEY = "reviews.stylists_delete";
    
    
    

    private final Connection connection;
    
    private final AvailabilityService availabilityService;
    private final StylistService stylistService;

    public MessagingService(Connection connection,
                            AvailabilityService availabilityService,
                            @Lazy StylistService stylistService) {
    	
        this.connection = connection;
        this.availabilityService = availabilityService;
        this.stylistService = stylistService;
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
            
            switch(routingKey) {
                case STYLISTS_AVAILABILITIES_MONTHLY_ADD_ROUTING_KEY:
	            	availabilityService.createMonthlyAvailabilities(
                            Objects.requireNonNull(GlamoorJsonMapper.fromJson(
                                    message, MonthlyAvailabilityRequest.class)));
	            	break;
	            case STYLISTS_AVAILABILITIES_DAILY_ADD_ROUTING_KEY:
	            	availabilityService.createDailyAvailabilities(
                            Objects.requireNonNull(GlamoorJsonMapper.fromJson(
                                    message, DailyAvailabilityRequest.class)));
	            	break;
	            case STYLISTS_AVAILABILITIES_MONTHLY_DELETE_ROUTING_KEY:
	            	propertiesHeaders = delivery.getProperties().getHeaders();
	            	availabilityService.deleteMonthlyAvailabilities(
	            			propertiesHeaders.get("stylistId").toString(), 
	            			propertiesHeaders.get("serviceProviderId").toString(),
                            Objects.requireNonNull(GlamoorJsonMapper.fromJson(message, LocalDate.class)));
	            	break;
	            case STYLISTS_AVAILABILITIES_DAILY_DELETE_ROUTING_KEY:
	            	propertiesHeaders = delivery.getProperties().getHeaders();
	            	availabilityService.deleteDailyAvailabilities(
	            			propertiesHeaders.get("stylistId").toString(), 
	            			propertiesHeaders.get("serviceProviderId").toString(), 
	            			GlamoorJsonMapper.fromJson(message, LocalDate.class));
	            	break;
	            case STYLISTS_AVAILABILITIES_BOOK_ROUTING_KEY:
	            	propertiesHeaders = delivery.getProperties().getHeaders();
	            	availabilityService.bookSlot(
	            			propertiesHeaders.get("stylistId").toString(), 
	            			propertiesHeaders.get("serviceProviderId").toString(),
	            			(LocalDate) propertiesHeaders.get("date"),
                            Objects.requireNonNull(GlamoorJsonMapper.fromJson(message, TimeSlot.class)));
	            	break;
	            case STYLISTS_AVAILABILITIES_UNBOOK_ROUTING_KEY:
	            	propertiesHeaders = delivery.getProperties().getHeaders(); 
	            	availabilityService.unbookSlot(
	            			propertiesHeaders.get("stylistId").toString(), 
	            			propertiesHeaders.get("serviceProviderId").toString(),
	            			(LocalDate) propertiesHeaders.get("date"),
                            Objects.requireNonNull(GlamoorJsonMapper.fromJson(message, TimeSlot.class)));
	            	break;
	            case STYLISTS_SERVICES_UPDATE_ROUTING_KEY:
	            	stylistService.updateService(GlamoorJsonMapper.fromJson(message, GlamoorService.class));
            }
            
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        System.out.println("Started receiving messages from queue: " + QUEUE_NAME);
    }
    
}

