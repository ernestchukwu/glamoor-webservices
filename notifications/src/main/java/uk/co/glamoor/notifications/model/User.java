package uk.co.glamoor.notifications.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "users")
public class User {
	
	@Id
	private String id;
	private String email;
	private String phone;
	private UserSettings settings = new UserSettings();

	@Data
	public static class UserSettings {

		private boolean pushNotifications = true;
		private boolean emailNotifications = true;
		private boolean inAppNotifications = true;

		AppointmentReminderDuration appointmentReminderDuration = AppointmentReminderDuration.TWO_HRS_BEFORE;
	}
}
