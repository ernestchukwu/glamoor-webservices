package uk.co.glamoor.posts.mapper;

import uk.co.glamoor.posts.dto.PostDTO;
import uk.co.glamoor.posts.model.Post;

public class PostMapper {

    public static PostDTO toDto(Post post) {
        PostDTO dto = new PostDTO();

        dto.setId(post.getId());
        dto.setPhotos(post.getPhotos());
        dto.setTime(post.getTime());
        dto.setDescription(post.getDescription());
        dto.setStylist(post.getStylist());
        dto.setServices(post.getServices());
        dto.setLiked(post.isLiked());
        dto.setSaved(post.isSaved());
        dto.setLikes(post.getLikes() == null ? 0 : post.getLikes());

        return dto;
    }
}
