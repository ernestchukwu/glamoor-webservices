package uk.co.glamoor.notifications.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.co.glamoor.notifications.model.Notification;


@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
	List<Notification> findByRecipientOrderByTimeDesc(String recipientId, Pageable pageable);
	void deleteNotificationsByRecipient(String recipientId);
}
