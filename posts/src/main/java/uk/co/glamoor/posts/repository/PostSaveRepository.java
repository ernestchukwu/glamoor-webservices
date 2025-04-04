package uk.co.glamoor.posts.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.co.glamoor.posts.model.PostSave;

@Repository
public interface PostSaveRepository extends MongoRepository<PostSave, String>{
	Optional<PostSave> findByCustomerAndPost(String customerId, String postId);
}
