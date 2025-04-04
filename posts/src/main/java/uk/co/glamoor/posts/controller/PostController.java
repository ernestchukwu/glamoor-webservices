package uk.co.glamoor.posts.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.github.javafaker.Faker;
import io.trbl.blurhash.BlurHash;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import uk.co.glamoor.posts.configuration.AppConfig;
import uk.co.glamoor.posts.dto.PostDTO;
import uk.co.glamoor.posts.mapper.PostMapper;
import uk.co.glamoor.posts.model.Location;
import uk.co.glamoor.posts.model.Post;
import uk.co.glamoor.posts.repository.PostRepository;
import uk.co.glamoor.posts.service.LocationService;
import uk.co.glamoor.posts.service.PostService;
import uk.co.glamoor.posts.service.RequestService;

import javax.imageio.ImageIO;

@RestController
@RequestMapping("/api/posts")
@Validated
public class PostController {
	
	private final PostService postService;
	private final LocationService locationService;
	private final AppConfig appConfig;
	private final RequestService requestService;
	private final PostRepository postRepository;
	
	public PostController(PostService postService, LocationService locationService, AppConfig appConfig, RequestService requestService, PostRepository postRepository) {
		
		this.postService = postService;
        this.locationService = locationService;
        this.appConfig = appConfig;
        this.requestService = requestService;
        this.postRepository = postRepository;
    }

	@GetMapping("/generate-data")
	public Mono<?> generatePosts() {
		Post.Stylist s1 = new Post.Stylist();
		s1.setId("676a4d91d7aea2208d674d84");
		s1.setAlias("ricardo");

		Post.Stylist s2 = new Post.Stylist();
		s2.setId("676a4d8ed7aea2208d674d51");
		s2.setAlias("zachery");

		Post.Service se00 = new Post.Service();
		se00.setId("6732c50b624ee92b60601266");
		se00.setName("Dying Services");

		Post.Service se01 = new Post.Service();
		se01.setId("6732c50b624ee92b60601264");
		se01.setName("Silk Presses");

		Post.Service se02 = new Post.Service();
		se02.setId("6732c50b624ee92b60601265");
		se02.setName("Weaves");

		Post.Service se03 = new Post.Service();
		se03.setId("6732c50b624ee92b6060125a");
		se03.setName("Beard Trimming");

		Post.Service se04 = new Post.Service();
		se04.setId("6732c50b624ee92b60601262");
		se04.setName("Knotless Braids");


		Post.Service se10 = new Post.Service();
		se10.setId("6732c50b624ee92b6060126c");
		se10.setName("LED Light Therapy");

		Post.Service se11 = new Post.Service();
		se11.setId("6732c50b624ee92b6060125b");
		se11.setName("Hot Towel Shaves");

		Post.Service se12 = new Post.Service();
		se12.setId("6732c50b624ee92b6060125e");
		se12.setName("Box Braids");

		Post.Service se13 = new Post.Service();
		se13.setId("6732c50b624ee92b6060125d");
		se13.setName("Scalp Treatments");

		Post.Service se14 = new Post.Service();
		se14.setId("6732c50b624ee92b60601265");
		se14.setName("Weaves");

		Post.Location l0 = new Post.Location();
		l0.setType("Point");
		l0.setCoordinates(List.of(-1.593579884964738, 53.87391469668939));

		Post.Location l1 = new Post.Location();
		l1.setType("Point");
		l1.setCoordinates(List.of(-8.463522242426938, 52.4608213975874));

		List<Post.Stylist> stylists = List.of(s1, s2);
		List<List<Post.Service>> services = List.of(List.of(se00, se01, se02, se03, se04),
				List.of(se10, se11, se12, se13, se14));
		List<Post.Location> locations = List.of(l0, l1);
		boolean first = true;
		Post post = null;

		ImageIO.scanForPlugins();

		File dir = new File("src/main/resources/static/images/posts");
		File[] files = dir.listFiles();
		if (files == null) return Mono.justOrEmpty("");
		Arrays.sort(files, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
		for (File file : files) {
			if (file.getName().endsWith("0.webp")) {
				if (post != null) {
					postRepository.save(post);
				}
				post = new Post();
				post.setId(file.getName().replace("0.webp", ""));
				first = Math.random() < 0.5;
			}
			post.setStylist(stylists.get(first ? 0 : 1));
			post.setStatus(Post.Status.ACTIVE);
			post.setLocation(locations.get(first ? 0 : 1));
			post.setDescription("");
			try {
				BufferedImage image = ImageIO.read(file);
				post.getPhotos().add(BlurHash.encode(image));
			} catch (Exception e) {
				System.out.println(file.getAbsolutePath());
				System.out.println(e.getMessage());
			}
			post.setServices(List.of(first ? services.get(0).get((int) (Math.random() * 5)) :
					services.get(1).get((int) (Math.random() * 5))));
			post.setDescription(new Faker().lorem().sentence());
		}
		if (post != null) {
			postRepository.save(post);
		}

		return Mono.justOrEmpty("");
	}

	@GetMapping("/latest")
	public ResponseEntity<List<PostDTO>> getLatestPosts(
			ServerWebExchange exchange,
			@RequestParam(required = false) @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90")
			@DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90")
			Double lat,
			@RequestParam(required = false) @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180")
			@DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180")
			Double lng,
			@RequestParam @Min(value=0) int offset,
			@RequestParam(required = false) String customerId,
			@RequestParam boolean homeView) {

		if (lat == null || lng == null) {
			double[] coordinates = locationService.getLocation(requestService.getIPAddress(exchange));
			if (coordinates != null) {
				lng = coordinates[0];
				lat = coordinates[1];
			} else {
				lng = appConfig.getDefaultLongitude();
				lat = appConfig.getDefaultLatitude();
			}
		}

		Location location = new Location();
		location.setCoordinates(List.of(lng, lat));
		
		return ResponseEntity.ok(postService
				.getLatestPosts(location, customerId, offset, homeView)
				.stream()
				.map(PostMapper::toDto).toList());
	}
	
	@GetMapping("/{stylistId}")
	public ResponseEntity<List<PostDTO>> getPosts(
			@PathVariable @NotBlank String stylistId,
			@RequestParam @Min(value=0) int offset,
			@RequestParam (required = false) String customerId) {
		
		return ResponseEntity.ok(postService
				.getPosts(stylistId, customerId, offset));
	}
	
	@PostMapping("/{postId}/like")
	public ResponseEntity<?> likePost(
			@RequestHeader(value = "X-User-Id", required = false) String id,
			@PathVariable @NotBlank String postId,
			@RequestParam @NotBlank String customerId) {

		postService.validateRequestSender(id, customerId);
		postService.likePost(customerId, postId);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/{postId}/like")
	public ResponseEntity<?> unlikePost(
			@RequestHeader(value = "X-User-Id", required = false) String id,
			@PathVariable @NotBlank String postId,
			@RequestParam @NotBlank String customerId) {

		postService.validateRequestSender(id, customerId);
		postService.unlikePost(customerId, postId);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/{postId}/save")
	public ResponseEntity<?> savePost(
			@RequestHeader(value = "X-User-Id", required = false) String id,
			@PathVariable @NotBlank String postId,
			@RequestParam @NotBlank String customerId) {

		postService.validateRequestSender(id, customerId);
		postService.savePost(customerId, postId);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/{postId}/save")
	public ResponseEntity<?> unsavePost(
			@RequestHeader(value = "X-User-Id", required = false) String id,
			@PathVariable @NotBlank String postId,
			@RequestParam @NotBlank String customerId) {

		postService.validateRequestSender(id, customerId);
		postService.unsavePost(customerId, postId);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/saved")
	public ResponseEntity<List<PostDTO>> getSavedPosts(
			@RequestHeader(value = "X-User-Id", required = false) String id,
	        @RequestParam @NotBlank String customerId,
	        @RequestParam(defaultValue = "0") int offset) {

		postService.validateRequestSender(id, customerId);
	    List<PostDTO> posts = postService.getSavedPostsByCustomer(customerId, offset);
	    return ResponseEntity.ok(posts);
	}
}
