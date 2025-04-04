package uk.co.glamoor.posts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.co.glamoor.posts.model.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String>{
	void deleteByStylistId(String stylistId);
	
}
