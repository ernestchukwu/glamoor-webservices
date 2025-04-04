package uk.co.glamoor.reviews.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.co.glamoor.reviews.config.ReviewsConfig;
import uk.co.glamoor.reviews.model.ReviewReply;
import uk.co.glamoor.reviews.repository.ReviewReplyRepository;

import java.util.List;

@Service
public class ReviewReplyService {

    private final ReviewReplyRepository reviewReplyRepository;
    private final ReviewsConfig reviewsConfig;

    public ReviewReplyService(ReviewReplyRepository reviewReplyRepository, ReviewsConfig reviewsConfig) {
        this.reviewReplyRepository = reviewReplyRepository;
        this.reviewsConfig = reviewsConfig;
    }

    public List<ReviewReply> getReviewReplies(String ratingId, int offset) {
        int batchSize = reviewsConfig.getReviewRepliesRequestBatchSize();
        Pageable pageable = PageRequest.of(offset / batchSize, batchSize, Sort.by(Sort.Direction.DESC, "time"));
        return reviewReplyRepository.findByRating(ratingId, pageable).getContent();
    }

}
