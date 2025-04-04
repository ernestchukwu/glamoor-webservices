package uk.co.glamoor.notifications.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import uk.co.glamoor.notifications.config.AppConfig;
import uk.co.glamoor.notifications.model.Notification;
import uk.co.glamoor.notifications.model.NotificationType;
import uk.co.glamoor.notifications.model.User;
import uk.co.glamoor.notifications.model.messaging.BookingMessage;
import uk.co.glamoor.notifications.repository.CustomNotificationRepository;
import uk.co.glamoor.notifications.repository.NotificationRepository;

@Service
public class NotificationService {
	
	private final NotificationRepository notificationRepository;
	private final CustomNotificationRepository customNotificationRepository;
	private final UserService userService;
	private final DeviceService deviceService;
	private final EmailService emailService;
	private final PushNotificationService pushNotificationService;
	private final AppConfig appConfig;
	
	public NotificationService(NotificationRepository notificationRepository,
			CustomNotificationRepository customNotificationRepository,
			UserService userService,
			DeviceService deviceService,
			EmailService emailService,
			PushNotificationService pushNotificationService,
			AppConfig appConfig) {
		
		this.notificationRepository = notificationRepository;
		this.customNotificationRepository = customNotificationRepository;
		this.userService = userService;
		this.deviceService = deviceService;
		this.emailService = emailService;
		this.pushNotificationService = pushNotificationService;
		this.appConfig = appConfig;
	}

    public List<Notification> getNotifications(String recipientId, int offset) {
        
    	int batchSize = appConfig.getNotificationRequestBatchSize();
		Pageable pageable = PageRequest.of(offset, batchSize);
		
        return notificationRepository.findByRecipientOrderByTimeDesc(recipientId, pageable);
    }
    
    public void clearNotifications(String userId) {
    	notificationRepository.deleteNotificationsByRecipient(userId);;
    }
    
    public boolean markAsRead(String id) {
        return customNotificationRepository.markAsRead(id);
    }
    
    public boolean markAsSeen(String recipientId, LocalDateTime time) {
        return customNotificationRepository.markAsSeen(recipientId, time);
    }
	
	public void addNotification(Notification notification) {
		notificationRepository.save(notification);
	}
	
	public void sendBookingNotification(
			BookingMessage bookingMessage, String sender, NotificationType notificationType) {
		
		List<User> recipients = new ArrayList<>();
		
//		if (bookingMessage.getCustomer().getId().equals(sender)) {
//			recipients.add(bookingMessage.getServiceProvider());
//			recipients.add(bookingMessage.getStylist());
//		} else {
//			recipients.add(bookingMessage.getCustomer());
//		}
		
		List<User> users = userService.findUsers(recipients.stream()
				.map(recipient -> recipient.getId())
				.collect(Collectors.toList()));
		
		for (User user : users) {
			if (user.getSettings().isEmailNotifications()) {
				switch (notificationType) {
					case BOOKING_CANCELLATION:
						emailService.sendBookingCancellationEmail(bookingMessage, user.getEmail());
					default:
						break;
				}
			}
			if (user.getSettings().isPushNotifications()) {
				List<String> tokens = deviceService
						.findDevices(user.getId())
						.stream()
						.map(device -> device.getDeviceToken())
						.collect(Collectors.toList());
				switch (notificationType) {
					case BOOKING_CANCELLATION:
						pushNotificationService.sendBookingCancelationNotification(bookingMessage, tokens);
					default:
						break;
				}
			}
		}

	}
	
}
