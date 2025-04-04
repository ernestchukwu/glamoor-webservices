package uk.co.glamoor.bookings.mapper;

import uk.co.glamoor.bookings.dto.request.MessageRequest;
import uk.co.glamoor.bookings.dto.response.MessageResponse;
import uk.co.glamoor.bookings.model.Message;

public class MessageMapper {

	public static Message mapToMessage(MessageRequest messageRequest) {

		if (messageRequest == null) return null;
	
    	Message message = new Message();
		
    	message.setId(messageRequest.getId());
		message.setSender(messageRequest.getSender());
		message.setMessage(messageRequest.getMessage());
		message.setTime(messageRequest.getTime());
		message.setContainsImage(messageRequest.isContainsImage());
		message.setSeen(messageRequest.isSeen());
		
		return message;
    }
    
    public static MessageResponse mapToMessageResponse(Message message) {

		if (message == null) return null;

		MessageResponse messageResponse = new MessageResponse();
    	
    	messageResponse.setId(message.getId());
    	messageResponse.setSender(message.getSender());
    	messageResponse.setMessage(message.getMessage());
    	messageResponse.setTime(message.getTime());
    	messageResponse.setContainsImage(message.isContainsImage());
    	messageResponse.setSeen(message.isSeen());
		
		return messageResponse;
    }
}
