package uk.co.glamoor.notifications.mapper;

import uk.co.glamoor.notifications.dto.NotificationDTO;
import uk.co.glamoor.notifications.model.Notification;

public class NotificationMapper {

	public static NotificationDTO toDTO(Notification notification) {

		if (notification == null) return null;
    	
		NotificationDTO dto = new NotificationDTO();
		
		dto.setId(notification.getId());
		dto.setTitle(notification.getTitle());
		dto.setMessage(notification.getMessage());
		dto.setRecipientId(notification.getRecipient());
		dto.setType(notification.getType());
		dto.setActionUrl(notification.getActionUrl());
		dto.setSeen(notification.isSeen());
		dto.setRead(notification.isRead());
		dto.setTime(notification.getTime());
		
		return dto;
	}
}
