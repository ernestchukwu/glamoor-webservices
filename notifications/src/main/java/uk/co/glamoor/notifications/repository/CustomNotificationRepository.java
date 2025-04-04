package uk.co.glamoor.notifications.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.UpdateResult;

import uk.co.glamoor.notifications.model.Notification;

@Service
public class CustomNotificationRepository {

	private final MongoTemplate mongoTemplate;
	
	public CustomNotificationRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	public boolean markAsSeen(String recipientId, LocalDateTime time) {
		Query query = new Query(Criteria.where("timeCreated")
				.lte(time).and("recipientId").is(recipientId));
        
        Update update = new Update()
                .set("isSeen", true);
        
        UpdateResult result = mongoTemplate.updateMulti(query, update, Notification.class);
        
        return result.getModifiedCount() > 0;
	}

	public boolean markAsRead(String notificationId) {
        
		Query query = new Query(Criteria.where("_id").is(notificationId));
        
        Update update = new Update()
                .set("isRead", true)
                .set("timeUpdated", LocalDateTime.now());
        
        UpdateResult result = mongoTemplate.updateFirst(query, update, Notification.class);
        
        return result.getModifiedCount() > 0;
		
	}
		
}
