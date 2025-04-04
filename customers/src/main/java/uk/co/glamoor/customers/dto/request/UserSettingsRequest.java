package uk.co.glamoor.customers.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uk.co.glamoor.customers.enums.AppointmentReminderDuration;
import uk.co.glamoor.customers.enums.DarkTheme;
import uk.co.glamoor.customers.model.NotificationSettings;
import uk.co.glamoor.customers.validation.EnumValidator;

@Data
public class UserSettingsRequest {

	@Valid
	private final NotificationSettings notificationSettings = new NotificationSettings();

	@NotNull
	@EnumValidator(enumClass = AppointmentReminderDuration.class)
	private final AppointmentReminderDuration appointmentReminderDuration = AppointmentReminderDuration.TWO_HRS_BEFORE;

	@NotNull
	@EnumValidator(enumClass = DarkTheme.class)
	private final DarkTheme darkTheme = DarkTheme.OFF;

}
