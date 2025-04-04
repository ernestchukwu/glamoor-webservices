package uk.co.glamoor.reviews.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.co.glamoor.reviews.model.ReviewReply;

@Repository
public interface ReviewReplyRepository extends MongoRepository<ReviewReply, String> {
    // Custom query method to fetch replies by ratingId, ordered by time descending
    @Query(value = "{ 'ratingId': ?0 }", sort = "{ 'time': -1 }")
    Page<ReviewReply> findByRating(String ratingId, Pageable pageable);
}
