package uk.co.glamoor.notifications.dto;

import java.time.LocalDateTime;

import lombok.Data;
import uk.co.glamoor.notifications.model.NotificationType;

@Data
public class NotificationDTO {
	
	private String id;
	private String title;
	private String message;
	private String recipientId;
	private NotificationType type;
	private String actionUrl;
	private boolean isRead;
	private boolean isSeen;
	private LocalDateTime time;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getRecipientId() {
		return recipientId;
	}
	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}
	public NotificationType getType() {
		return type;
	}
	public void setType(NotificationType type) {
		this.type = type;
	}
	public String getActionUrl() {
		return actionUrl;
	}
	public void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}
	public boolean isRead() {
		return isRead;
	}
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	public boolean isSeen() {
		return isSeen;
	}
	public void setSeen(boolean isSeen) {
		this.isSeen = isSeen;
	}
	public LocalDateTime getTime() {
		return time;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	
	
}
