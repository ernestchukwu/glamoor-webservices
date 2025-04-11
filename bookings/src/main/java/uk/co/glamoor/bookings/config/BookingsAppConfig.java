package uk.co.glamoor.bookings.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bookings-app")
@Data
@RefreshScope
public class BookingsAppConfig {

	private int bookingsRequestBatchSize;
	private int bookingsRequestBatchSizeForHomeScreen;
	private int messagesRequestBatchSize;
	private int futureBookingDaysExtent;
	private int timeSlotEscrowDurationSeconds;

}
