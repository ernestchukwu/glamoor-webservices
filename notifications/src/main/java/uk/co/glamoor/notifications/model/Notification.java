package uk.co.glamoor.notifications.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "notifications")
public class Notification {
	
	@Id
	private String id;
	private String title;
	private String message;
	private String recipient;
	private NotificationType type;
	private String actionUrl;
	private boolean isRead;
	private boolean isSeen;
	private LocalDateTime time;
	private LocalDateTime timeUpdated;
	
}
