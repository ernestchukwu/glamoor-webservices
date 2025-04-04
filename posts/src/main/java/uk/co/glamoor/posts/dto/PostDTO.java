package uk.co.glamoor.posts.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import lombok.Data;
import uk.co.glamoor.posts.model.Post.Service;
import uk.co.glamoor.posts.model.Post.Status;
import uk.co.glamoor.posts.model.Post.Stylist;

@Data
public class PostDTO {

	@Id
	private String id;
	private List<Service> services;
	private Stylist stylist;
	private String description;
	private LocalDateTime time;
	private List<String> photos;
	private boolean liked;
	private boolean saved;
	private int likes;

}
