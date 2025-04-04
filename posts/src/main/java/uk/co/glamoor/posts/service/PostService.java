package uk.co.glamoor.posts.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import uk.co.glamoor.posts.configuration.PostsConfig;
import uk.co.glamoor.posts.exception.EntityNotFoundException;
import uk.co.glamoor.posts.exception.EntityType;
import uk.co.glamoor.posts.model.Post;
import uk.co.glamoor.posts.repository.*;
import uk.co.glamoor.posts.configuration.AppConfig;
import uk.co.glamoor.posts.dto.PostDTO;
import uk.co.glamoor.posts.model.Location;
import uk.co.glamoor.posts.model.PostLike;
import uk.co.glamoor.posts.model.PostSave;

@Service
public class PostService {
	
	private final PostRepository postRepository;
	private final CustomPostRepository customPostRepository;
	private final PostLikeRepository postLikeRepository;
	private final PostSaveRepository postSaveRepository;
	private final AppConfig appConfig;
	private final PostsConfig postsConfig;
	
	public PostService(
            PostRepository postRepository,
            CustomPostRepository customPostRepository,
            PostLikeRepository postLikeRepository,
            PostSaveRepository postSaveRepository,
            AppConfig appConfig, PostsConfig postsConfig) {
		
		this.postRepository = postRepository;
		this.customPostRepository = customPostRepository;
		this.postLikeRepository = postLikeRepository;
		this.postSaveRepository = postSaveRepository;
        this.appConfig = appConfig;
        this.postsConfig = postsConfig;
    }

	private void validateCustomerIdAndPostId(String customerId, String postId) {
		if (!postRepository.existsById(postId)) {
			throw new EntityNotFoundException(postId, EntityType.POST);
		}
	}

	public List<Post> getLatestPosts(Location location, String customerId,
									 int offset, boolean homeView) {
		return customPostRepository.getLatestPosts(location, customerId, offset,
				homeView ? postsConfig.getPostsRequestBatchSizeForHomeView() : postsConfig.getPostsRequestBatchSize(),
						appConfig.getQueryResultsMaxDistance());
	}
	
	public List<PostDTO> getPosts(String stylistId, String customerId,
								  int offset) {
		return customPostRepository.getPosts(stylistId, customerId, offset,
				postsConfig.getPostsRequestBatchSize());
	}
	
	public List<PostDTO> getSavedPostsByCustomer(String customerId,
												 int offset) {
		return customPostRepository.getSavedPostsByCustomer(customerId, offset,
				postsConfig.getPostsRequestBatchSize());
	}
	
	public void likePost(String customerId, String postId) {

		Post post = postRepository.findById(postId).orElseThrow(
				() -> new EntityNotFoundException(postId, EntityType.POST)
		);

		Optional<PostLike> existingLike = postLikeRepository.findByCustomerAndPost(customerId, postId);
        if (existingLike.isEmpty()) {
        	PostLike postLike = new PostLike();
            postLike.setCustomer(customerId);
            postLike.setPost(postId);
            postLikeRepository.save(postLike);
			post.setLikes(post.getLikes() == null ? 1 : post.getLikes() + 1);
			postRepository.save(post);
        }
	}
	
	public void unlikePost(String customerId, String postId) {

		Post post = postRepository.findById(postId).orElseThrow(
				() -> new EntityNotFoundException(postId, EntityType.POST)
		);
		Optional<PostLike> existingLike = postLikeRepository.findByCustomerAndPost(customerId, postId);
        existingLike.ifPresent(value -> {
			postLikeRepository.delete(value);
			post.setLikes(post.getLikes() == null || post.getLikes() == 0 ? 0 : post.getLikes() - 1);
			postRepository.save(post);
		});

	}
	
	public void savePost(String customerId, String postId) {
		validateCustomerIdAndPostId(customerId, postId);
		Optional<PostSave> existingSave = postSaveRepository.findByCustomerAndPost(customerId, postId);
        if (existingSave.isEmpty()) {
        	PostSave postSave = new PostSave();
            postSave.setCustomer(customerId);
            postSave.setPost(postId);
            postSaveRepository.save(postSave);
        }
	}
	
	public void unsavePost(String customerId, String postId) {
		validateCustomerIdAndPostId(customerId, postId);
		Optional<PostSave> existingSave = postSaveRepository.findByCustomerAndPost(customerId, postId);
        existingSave.ifPresent(postSaveRepository::delete);
	}
	
	public void deleteStylist(String stylistId) {
		postRepository.deleteByStylistId(stylistId);
	}
	
	public void updateStylistAlias(String stylistId, String alias) {
		customPostRepository.updateStylistAlias(stylistId, alias);
	}

	public void validateRequestSender(String id1, String id2) {
		if (id1 == null || !id1.equals(id2)) {
			throw new IllegalArgumentException("Unauthorised access.");
		}
	}
}
