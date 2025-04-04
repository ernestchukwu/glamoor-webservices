package uk.co.glamoor.bookings.service;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.AMQP.BasicProperties;

import jakarta.annotation.PostConstruct;
import uk.co.glamoor.bookings.mapper.GlamoorJsonMapper;
import uk.co.glamoor.bookings.model.Stylist;
import uk.co.glamoor.bookings.model.StylistServiceSpecification;
import uk.co.glamoor.bookings.model.Customer;
import uk.co.glamoor.bookings.model.GlamoorService;
import uk.co.glamoor.bookings.model.HomeServiceSpecification;
import uk.co.glamoor.bookings.model.ServiceProvider;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {

    private static final String EXCHANGE_NAME = "booking-topic-exchange";
    private static final String QUEUE_NAME = "booking-queue";
    private static final String ROUTING_KEY_PATTERN = "bookings.*";

    public static final String STYLIST_EXCHANGE = "stylist-topic-exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification-topic-exchange";

    private final Logger logger = LoggerFactory.getLogger(MessagingService.class);

    private static final String BOOKINGS_STYLISTS_NEW_ROUTING_KEY = "bookings.stylists_new";
    private static final String BOOKINGS_STYLISTS_UPDATE_ROUTING_KEY = "bookings.stylists_update";
    private static final String BOOKINGS_STYLISTS_ANONYMISE_ROUTING_KEY = "bookings.stylists_anonymise";

    private static final String BOOKINGS_CUSTOMERS_NEW_ROUTING_KEY = "bookings.customers_new";
    private static final String BOOKINGS_CUSTOMERS_UPDATE_ROUTING_KEY = "bookings.customers_update";
    private static final String BOOKINGS_CUSTOMERS_ANONYMISE_ROUTING_KEY = "bookings.customers_anonymise";

    private static final String BOOKINGS_STYLISTS_SERVICE_SPECIFICATION_NEW_ROUTING_KEY = "bookings.stylists_service-specifications_new";
    private static final String BOOKINGS_STYLISTS_SERVICE_SPECIFICATION_UPDATE_ROUTING_KEY = "bookings.stylists_service-specifications_update";
    private static final String BOOKINGS_STYLISTS_SERVICE_SPECIFICATION_DELETE_ROUTING_KEY = "bookings.stylists_service-specifications_delete";

    private static final String BOOKINGS_STYLISTS_HOME_SERVICE_SPECIFICATION_NEW_ROUTING_KEY = "bookings.stylists_home-service-specifications_new";
    private static final String BOOKINGS_STYLISTS_HOME_SERVICE_SPECIFICATION_UPDATE_ROUTING_KEY = "bookings.stylists_home-service-specifications_update";
    private static final String BOOKINGS_STYLISTS_HOME_SERVICE_SPECIFICATION_DELETE_ROUTING_KEY = "bookings.stylists_home-service-specifications_delete";

    private static final String BOOKINGS_STYLISTS_SERVICE_PROVIDER_NEW_ROUTING_KEY = "bookings.stylists_service-providers_new";
    private static final String BOOKINGS_STYLISTS_SERVICE_PROVIDER_UPDATE_ROUTING_KEY = "bookings.stylists_service-providers_update";
    private static final String BOOKINGS_STYLISTS_SERVICE_PROVIDER_DELETE_ROUTING_KEY = "bookings.stylists_service-providers_delete";

    private static final String BOOKINGS_SERVICES_UPDATE_ROUTING_KEY = "bookings.services_update";

    // For stylists service
    public static final String STYLISTS_AVAILABILITIES_MONTHLY_ADD_ROUTING_KEY = "stylists.availabilities_monthly_add";
    public static final String STYLISTS_AVAILABILITIES_DAILY_ADD_ROUTING_KEY = "stylists.availabilities_daily_add";
    public static final String STYLISTS_AVAILABILITIES_MONTHLY_DELETE_ROUTING_KEY = "stylists.availabilities_monthly_delete";
    public static final String STYLISTS_AVAILABILITIES_DAILY_DELETE_ROUTING_KEY = "stylists.availabilities_daily_delete";
    public static final String STYLISTS_AVAILABILITIES_BOOK_ROUTING_KEY = "stylists.availabilities_book";
    public static final String STYLISTS_AVAILABILITIES_UNBOOK_ROUTING_KEY = "stylists.availabilities_unbook";


    // For notifications service
    public static final String NOTIFICATIONS_BOOKINGS_NEW_ROUTING_KEY = "notifications.bookings_new";
    public static final String NOTIFICATIONS_BOOKINGS_CANCELLED_ROUTING_KEY = "notifications.bookings_cancelled";


    private final Connection connection;

    private final StylistService stylistService;
    private final CustomerService customerService;

    public MessagingService(Connection connection,
                            CustomerService customerService,
                            @Lazy StylistService stylistService) {

        this.connection = connection;
        this.customerService = customerService;
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
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY_PATTERN);
            System.out.println("Queue setup completed with routing key pattern: " + ROUTING_KEY_PATTERN);
        }
    }

    public void sendMessage(String exchangeName,
                            String routingKey,
                            String message,
                            Map<String, Object> headerProperties) {

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
    }

    public void sendMessage(String exchangeName, String routingKey, String message) {
        sendMessage(exchangeName, routingKey, message, null);
    }

    private void startReceiving() throws Exception {
        Channel channel = connection.createChannel();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            String routingKey = delivery.getEnvelope().getRoutingKey();

            Stylist stylist;
            ServiceProvider serviceProvider;
            Customer customer;
            HomeServiceSpecification homeServiceSpec;
            StylistServiceSpecification specification;
            Map<String, Object> props = delivery.getProperties().getHeaders();
            String stylistId = "";
            if (props != null) {
                if (props.get("stylistId") instanceof com.rabbitmq.client.LongString) {
                    stylistId = props.get("stylistId").toString();
                } else if (props.get("stylistId") instanceof String) {
                    stylistId = (String) props.get("stylistId");
                } else {
                    stylistId = String.valueOf(props.get("stylistId"));
                }
            }

            switch (routingKey) {
                case BOOKINGS_SERVICES_UPDATE_ROUTING_KEY:
                    stylistService.updateService(GlamoorJsonMapper.fromJson(message, GlamoorService.class));
                    break;
                case BOOKINGS_CUSTOMERS_NEW_ROUTING_KEY:
                    customer = GlamoorJsonMapper.fromJson(message, Customer.class);
                    customerService.addCustomer(customer);
                    break;
                case BOOKINGS_CUSTOMERS_UPDATE_ROUTING_KEY:
                    customer = GlamoorJsonMapper.fromJson(message, Customer.class);
                    customerService.updateCustomer(Objects.requireNonNull(customer));
                    break;
                case BOOKINGS_CUSTOMERS_ANONYMISE_ROUTING_KEY:
                    customerService.anonymiseCustomer(message);
                    break;
                case BOOKINGS_STYLISTS_NEW_ROUTING_KEY:

                    stylist = GlamoorJsonMapper.fromJson(message, Stylist.class);
                    stylistService.addStylist(stylist);
                    break;
                case BOOKINGS_STYLISTS_UPDATE_ROUTING_KEY:
                    stylist = GlamoorJsonMapper.fromJson(message, Stylist.class);

                    stylistService.updateStylist(Objects.requireNonNull(stylist));
                    break;
                case BOOKINGS_STYLISTS_ANONYMISE_ROUTING_KEY:
                    stylistService.anonymiseStylist(message);
                    break;
                case BOOKINGS_STYLISTS_HOME_SERVICE_SPECIFICATION_NEW_ROUTING_KEY:
                    stylistId = delivery.getProperties().getHeaders().get("stylistId").toString();
                    homeServiceSpec = GlamoorJsonMapper.fromJson(
                            message, HomeServiceSpecification.class);

                    stylistService.addHomeServiceSpecification(stylistId, Objects.requireNonNull(homeServiceSpec));
                    break;
                case BOOKINGS_STYLISTS_HOME_SERVICE_SPECIFICATION_UPDATE_ROUTING_KEY:
                    stylistId = delivery.getProperties().getHeaders().get("stylistId").toString();
                    homeServiceSpec = GlamoorJsonMapper.fromJson(
                            message, HomeServiceSpecification.class);

                    stylistService.updateHomeServiceSpecification(stylistId, homeServiceSpec);
                    break;
                case BOOKINGS_STYLISTS_HOME_SERVICE_SPECIFICATION_DELETE_ROUTING_KEY:
                    stylistId = delivery.getProperties().getHeaders().get("stylistId").toString();

                    stylistService.removeHomeServiceSpecification(stylistId, message);
                    break;
                case BOOKINGS_STYLISTS_SERVICE_SPECIFICATION_NEW_ROUTING_KEY:
                    stylistId = delivery.getProperties().getHeaders().get("stylistId").toString();
                    specification = GlamoorJsonMapper.fromJson(
                            message, StylistServiceSpecification.class);

                    stylistService.addServiceSpecification(stylistId, Objects.requireNonNull(specification));
                    break;
                case BOOKINGS_STYLISTS_SERVICE_SPECIFICATION_UPDATE_ROUTING_KEY:
                    stylistId = delivery.getProperties().getHeaders().get("stylistId").toString();
                    specification = GlamoorJsonMapper.fromJson(
                            message, StylistServiceSpecification.class);

                    stylistService.updateServiceSpecification(stylistId, specification);
                    break;
                case BOOKINGS_STYLISTS_SERVICE_SPECIFICATION_DELETE_ROUTING_KEY:
                    stylistId = delivery.getProperties().getHeaders().get("stylistId").toString();

                    stylistService.removeServiceSpecification(stylistId, message);
                    break;
                case BOOKINGS_STYLISTS_SERVICE_PROVIDER_NEW_ROUTING_KEY:
                    serviceProvider = GlamoorJsonMapper.fromJson(
                            message, ServiceProvider.class);

                    stylistService.addServiceProvider(stylistId, Objects.requireNonNull(serviceProvider));
                    break;
                case BOOKINGS_STYLISTS_SERVICE_PROVIDER_UPDATE_ROUTING_KEY:
                    serviceProvider = GlamoorJsonMapper.fromJson(
                            message, ServiceProvider.class);

                    stylistService.updateServiceProvider(stylistId, serviceProvider);
                    break;
                case BOOKINGS_STYLISTS_SERVICE_PROVIDER_DELETE_ROUTING_KEY:

                    stylistService.removeServiceProvider(stylistId, message);
                    break;
            }
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
        System.out.println("Started receiving messages from queue: " + QUEUE_NAME);
    }
}

