package uk.co.glamoor.notifications.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.co.glamoor.notifications.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

}
