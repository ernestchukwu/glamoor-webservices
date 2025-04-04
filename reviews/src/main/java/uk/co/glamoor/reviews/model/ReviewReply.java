package uk.co.glamoor.reviews.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Setter
@Getter
@Data
@Document(collection="review-replies")
public class ReviewReply {

    @Id
    private String id;
    private String rating;
    private String message;
    private String replier;
    private LocalDateTime time;
    private LocalDateTime timeUpdated;


}
