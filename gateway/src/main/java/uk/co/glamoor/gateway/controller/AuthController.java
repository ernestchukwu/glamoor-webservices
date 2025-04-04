package uk.co.glamoor.gateway.controller;

import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;
import uk.co.glamoor.gateway.dto.request.UserRequest;
import uk.co.glamoor.gateway.mapper.UserMapper;
import uk.co.glamoor.gateway.service.FirebaseService;
import uk.co.glamoor.gateway.service.UserService;

@RestController
@RequestMapping("/api/auth/users")
@RequiredArgsConstructor
public class AuthController {
	
	private final FirebaseService firebaseService;
	private final UserService userService;

	Logger logger = LoggerFactory.getLogger(AuthController.class);

	@GetMapping("/anonymous-token")
	public ResponseEntity<String> getAnonymousUserToken(@RequestParam String uid) {
		
		return ResponseEntity.ok(firebaseService.generateAnonymousToken(uid));
		
	}

	@PostMapping
	public Mono<ResponseEntity<?>> addUser(@RequestBody UserRequest userRequest) {
		return userService.addUser(UserMapper.toUser(userRequest))
				.thenReturn(ResponseEntity.noContent().build());
	}
	
	@DeleteMapping
	public Mono<ResponseEntity<?>> deleteUser(@RequestParam String userId,
										@RequestParam String uid) {
		return firebaseService.deleteUser(uid)
						.then(userService.removeUser(userId))
				.thenReturn(ResponseEntity.noContent().build());
		
	}

	@PatchMapping("/{userId}/update-email")
	public Mono<ResponseEntity<String>> updateEmail(
			@PathVariable String userId,
			@RequestParam @Email String email) {  // Added @Email validation

		return userService.findUser(userId)
				.flatMap(user -> firebaseService.updateUserEmail(user.getUid(), email)
						.then(Mono.just(user)))
				.flatMap(user -> firebaseService.generateCustomToken(user.getUid())
						.map(ResponseEntity::ok));
	}

}
