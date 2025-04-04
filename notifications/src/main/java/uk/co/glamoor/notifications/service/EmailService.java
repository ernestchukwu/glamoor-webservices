package uk.co.glamoor.notifications.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SesException;
import uk.co.glamoor.notifications.config.AppConfig;
import uk.co.glamoor.notifications.model.messaging.BookingMessage;
import uk.co.glamoor.notifications.template.EmailTemplate;

@Service
public class EmailService {
	
	AppConfig appConfig;
		
	private final Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	public EmailService(AppConfig appConfig) {
		this.appConfig = appConfig;
	}
	
	public void sendBookingCancellationEmail(BookingMessage bookingMessage, String recipient) {
		sendEmail(appConfig.getBookingsNotificationSenderEmail(),
				recipient,
				EmailTemplate.BOOKING_CANCELATION_SUBJECT, 
				EmailTemplate.getCancellationBodyText(
						bookingMessage, recipient.equals(bookingMessage.getCustomer().getId())),
				EmailTemplate.getCancellationBodyHtml(
						bookingMessage, recipient.equals(bookingMessage.getCustomer().getId())));
	}
	
	public void sendEmail(String sender, String recipient, 
			String subject, String bodyText, String bodyHtml) {
		
		try (SesClient sesClient = SesClient.create()) {
			
			Content subjectContent = Content.builder().data(subject).build();
			
			Body body = Body.builder()
					.text(Content.builder().data(bodyText).build())
					.html(Content.builder().data(bodyHtml).build())
					.build();
			
			Message message = Message.builder()
					.subject(subjectContent)
					.body(body)
					.build();
			
			SendEmailRequest emailRequest = SendEmailRequest.builder()
					.source(sender)
					.destination(Destination.builder().toAddresses(recipient).build())
					.message(message)
					.build();
			
			sesClient.sendEmail(emailRequest);
			
		} catch (SesException e) {
			logger.error("Failed to send email: {}", e.awsErrorDetails().errorMessage());
		}
	}
}
