package uk.co.glamoor.gateway.model.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
@RequiredArgsConstructor
public class User {

    @Id
    private final String id;
    private final String uid;
}
