package uk.co.glamoor.bookings.repository;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomMessageRepository {

    private final MongoTemplate mongoTemplate;

    public CustomMessageRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void markMessagesAsSeen(String customerId, String bookingId) {

        Bson filter = Filters.eq("_id", new ObjectId(bookingId));
        Bson update = Updates.set("messages.$[message].seen", true);

        UpdateOptions options = new UpdateOptions().arrayFilters(List.of(
                Filters.ne("message.sender", customerId)
        ));

        mongoTemplate.getDb()
                .getCollection("messages")
                .updateOne(filter, update, options);

    }
}
