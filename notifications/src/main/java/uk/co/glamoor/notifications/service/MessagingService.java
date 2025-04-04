package uk.co.glamoor.notifications.service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import jakarta.annotation.PostConstruct;
import uk.co.glamoor.notifications.dto.UserRequest;
import uk.co.glamoor.notifications.mapper.GlamoorJsonMapper;
import uk.co.glamoor.notifications.mapper.UserMapper;
import uk.co.glamoor.notifications.model.Device;
import uk.co.glamoor.notifications.model.Notification;
import uk.co.glamoor.notifications.model.NotificationType;
import uk.co.glamoor.notifications.model.messaging.BookingMessage;

@Service
public class MessagingService {
	
	
	private static final String EXCHANGE_NAME = "notification-topic-exchange";
    private static final String QUEUE_NAME = "notification-queue";
    private static final String ROUTING_KEY_PATTERN = "notifications.*";
    
    
    private final Logger logger = LoggerFactory.getLogger(MessagingService.class);
    
    public static final String NOTIFICATIONS_USERS_DELETE_ROUTING_KEY = "notifications.users_delete";
    public static final String NOTIFICATIONS_BOOKINGS_NEW_ROUTING_KEY = "notifications.bookings_new";
    public static final String NOTIFICATIONS_BOOKINGS_CANCEL_ROUTING_KEY = "notifications.bookings_cancel";
    public static final String NOTIFICATIONS_NEW_ROUTING_KEY = "notifications.new";
    public static final String NOTIFICATIONS_USERS_NEW_ROUTING_KEY = "notifications.users_new";
    public static final String NOTIFICATIONS_DEVICES_NEW_ROUTING_KEY = "notifications.devices_new";
    
    

    private final Connection connection;
    
    private final NotificationService notificationService;
    private final UserService userService;
    private final DeviceService deviceService;

    public MessagingService(Connection connection, 
    		NotificationService notificationService,
    		UserService userService,
    		DeviceService deviceService) {
    	
        this.connection = connection;
        this.notificationService = notificationService;
        this.userService = userService;
        this.deviceService = deviceService;
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
            logger.info("Queue setup completed with routing key pattern: " + ROUTING_KEY_PATTERN);
        }
    }
    
    
    private void startReceiving() throws Exception {
        Channel channel = connection.createChannel();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            String routingKey = delivery.getEnvelope().getRoutingKey();
                       
            Map<String, Object> propertiesHeaders ;
            switch(routingKey) {
	            
	            case NOTIFICATIONS_USERS_DELETE_ROUTING_KEY:
	            	userService.deleteUser(message);
		    		deviceService.clearDevices(message);
		    		notificationService.clearNotifications(message);
	            	break;

                case NOTIFICATIONS_BOOKINGS_NEW_ROUTING_KEY:
                    BookingMessage bookingMessage = GlamoorJsonMapper.fromJson(message, BookingMessage.class);

                    if (bookingMessage != null) {

                    }
                    break;

	            case NOTIFICATIONS_BOOKINGS_CANCEL_ROUTING_KEY:
	            	propertiesHeaders = delivery.getProperties().getHeaders();
	            	BookingMessage bookingMessage1 = GlamoorJsonMapper.fromJson(message, BookingMessage.class);
		    		
	    			if (bookingMessage1 == null) break;
		    		
	    			String initiator = (String) propertiesHeaders.get("initiator");
	    			
		    		notificationService.sendBookingNotification(bookingMessage1, initiator,
		    				NotificationType.BOOKING_CANCELLATION);
	    			break;
	    			
	            case NOTIFICATIONS_NEW_ROUTING_KEY:
	            	Notification notification = GlamoorJsonMapper.fromJson(message, Notification.class);
		    		
	    			if (notification == null) break;
	    			
	    			notificationService.addNotification(notification);
	    			
	            	break;
	            case NOTIFICATIONS_USERS_NEW_ROUTING_KEY:
	            	UserRequest userRequest = GlamoorJsonMapper.fromJson(message, UserRequest.class);
		    		
	    			if (userRequest == null) break;
	    			
		    		userService.addUser(UserMapper.toUser(userRequest));
		    		break;
	            case NOTIFICATIONS_DEVICES_NEW_ROUTING_KEY:
	            	Device device = GlamoorJsonMapper.fromJson(message, Device.class);
		    		
	    			if (device == null) break;
	    			
		    		deviceService.addDevice(device);
		    		break;
            }
            
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        logger.info("Started receiving messages from queue: " + QUEUE_NAME);
    }
    
	
}
