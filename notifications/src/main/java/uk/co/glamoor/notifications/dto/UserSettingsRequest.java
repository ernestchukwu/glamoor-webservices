package uk.co.glamoor.notifications.dto;

import lombok.Data;
import uk.co.glamoor.notifications.model.AppointmentReminderDuration;

@Data
public class UserSettingsRequest {
    private boolean pushNotifications = true;
    private boolean emailNotifications = true;
    private boolean inAppNotifications = true;

    AppointmentReminderDuration appointmentReminderDuration = AppointmentReminderDuration.TWO_HRS_BEFORE;
}
