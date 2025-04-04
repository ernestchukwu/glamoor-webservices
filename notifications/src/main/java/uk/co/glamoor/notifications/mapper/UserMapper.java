package uk.co.glamoor.notifications.mapper;

import uk.co.glamoor.notifications.dto.UserRequest;
import uk.co.glamoor.notifications.dto.UserSettingsRequest;
import uk.co.glamoor.notifications.model.User;

public class UserMapper {

	public static User toUser(UserRequest dto) {

		if (dto == null) return null;
    	
		User user = new User();
		
		user.setId(dto.getId());
		user.setEmail(dto.getEmail());
		if (dto.getPhone() != null) {
			user.setPhone(dto.getPhone().getFullNumber());
		}
		
		return user;
	}

	public static User.UserSettings toUserSettings(UserSettingsRequest userSettingsRequest) {
		User.UserSettings userSettings = new User.UserSettings();

		userSettings.setEmailNotifications(userSettingsRequest.isEmailNotifications());
		userSettings.setInAppNotifications(userSettingsRequest.isInAppNotifications());
		userSettings.setPushNotifications(userSettingsRequest.isPushNotifications());
		userSettings.setAppointmentReminderDuration(userSettingsRequest.getAppointmentReminderDuration());

		return userSettings;
	}
}
