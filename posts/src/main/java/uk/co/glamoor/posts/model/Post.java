package uk.co.glamoor.posts.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Setter
@Getter
@Data
@Document(collection = "posts")
public class Post {

	@Id
	private String id;

    private List<Service> services = new ArrayList<>();

    private Stylist stylist;

    private Location location;

    private String description;

    private List<String> photos = new ArrayList<>();

    private LocalDateTime time = LocalDateTime.now();

    private Integer likes;

    private boolean liked = false;
    private boolean saved = false;

    private Status status = Status.ACTIVE;

	@Setter
	@Getter
	@Data
    public static class Service {

		private String id;
        private String name;

    }

    @Setter
    @Getter
	@Data
    public static class Stylist {

    	private String id;
        private String alias;
        private String logo;

    }

	@Setter
	@Getter
	@Data
    public static class Location {
        private List<Double> coordinates;
        private String type = "Point";
    }
    
    public enum Status {
        ACTIVE,
        INACTIVE,
        DISABLED
    }
}
