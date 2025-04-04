package uk.co.glamoor.notifications.service;

import java.util.List;

import org.springframework.stereotype.Service;

import uk.co.glamoor.notifications.model.messaging.BookingMessage;

@Service
public class PushNotificationService {

	public void sendBookingCancelationNotification(
            BookingMessage bookingMessage, List<String> deviveTokens) {
		
		
	}
}
