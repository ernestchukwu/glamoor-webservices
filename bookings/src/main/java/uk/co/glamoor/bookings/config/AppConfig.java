package uk.co.glamoor.bookings.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bookings-app")
@Data
@RefreshScope
public class AppConfig {

	private Images images;
	private int bookingsRequestBatchSize;
	private int bookingsRequestBatchSizeForHomeScreen;
	private int messagesRequestBatchSize;
	private int futureBookingDaysExtent;
	private int timeSlotEscrowDurationSeconds;

	@Data
	public static class Images {
		private String defaultFormat;
		private Directories directories;
	}

	@Data
	public static class Directories {
		private String bookingMessages;
	}
}
