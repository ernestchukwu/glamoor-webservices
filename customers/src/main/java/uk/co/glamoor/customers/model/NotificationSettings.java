package uk.co.glamoor.customers.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class NotificationSettings {
	
	private boolean pushNotifications = true;
	private boolean emailNotifications = true;
	private boolean inAppNotifications = true;

}
