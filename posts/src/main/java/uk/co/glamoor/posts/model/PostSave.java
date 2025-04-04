package uk.co.glamoor.posts.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Getter
@Setter
@Document(collection = "post-saves")
public class PostSave {

	@Id
	private String id;
	private String customer;
	private String post;

}
