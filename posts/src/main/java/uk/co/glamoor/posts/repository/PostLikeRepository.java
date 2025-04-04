package uk.co.glamoor.posts.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.co.glamoor.posts.model.PostLike;

@Repository
public interface PostLikeRepository extends MongoRepository<PostLike, String>{
	Optional<PostLike> findByCustomerAndPost(String customerId, String postId);
}
