package uk.co.glamoor.gateway.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import uk.co.glamoor.gateway.model.entity.User;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByUid(String uid);
}
