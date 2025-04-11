package uk.co.glamoor.bookings.mapper;

import uk.co.glamoor.bookings.dto.request.MessageRequest;
import uk.co.glamoor.bookings.dto.response.MessageResponse;
import uk.co.glamoor.bookings.model.Message;


public class MessageMapper {

	public static Message toMessage(MessageRequest messageRequest) {

		if (messageRequest == null) return null;
	
    	Message message = new Message();

		message.setSender(messageRequest.getSender());
		message.setMessage(messageRequest.getMessage());
		message.setTime(messageRequest.getTime());
		message.setContainsImage(messageRequest.isContainsImage());
		message.setBooking(messageRequest.getBookingId());

		return message;
    }
    
    public static MessageResponse toMessageResponse(Message message) {

		if (message == null) return null;

		MessageResponse messageResponse = new MessageResponse();
    	
    	messageResponse.setId(message.getId());
    	messageResponse.setSender(message.getSender());
    	messageResponse.setMessage(message.getMessage());
    	messageResponse.setIsoTime(message.getTime().toString());
    	messageResponse.setContainsImage(message.isContainsImage());

		return messageResponse;
    }
}
