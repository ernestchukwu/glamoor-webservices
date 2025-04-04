package uk.co.glamoor.gateway.service;

import com.google.firebase.ErrorCode;
import com.google.firebase.auth.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class FirebaseService {

	private final Logger logger = LoggerFactory.getLogger(FirebaseService.class);

	public String generateAnonymousToken(String uid) {
        
		try {
	        if (uid == null || uid.isEmpty()) {
	            uid = "test-user-" + System.currentTimeMillis();
	        }
	
	//        Map<String, Object> claims = new HashMap<>();
	//        claims.put("role", "tester");
	//        claims.put("email_verified", true);
	
	        return FirebaseAuth.getInstance().createCustomToken(uid);
	        
		} catch (FirebaseAuthException e) {
			throw new RuntimeException("Error generating anonymous token: ", e);
		}
    }

	public Mono<Void> deleteUser(String uid) {
		return Mono.fromCallable(() -> {
					FirebaseAuth.getInstance().deleteUser(uid);
					return Mono.empty();
				})
				.then()
				.onErrorMap(FirebaseAuthException.class, e ->
						new RuntimeException("Error deleting auth user: " + e.getMessage(), e)
				);
	}

	public Mono<String> generateCustomToken(String uid) {
		return Mono.fromCallable(() -> FirebaseAuth.getInstance().createCustomToken(uid))
				.subscribeOn(Schedulers.boundedElastic())
				.onErrorMap(FirebaseAuthException.class, e ->
						new RuntimeException("Error creating custom token", e)
				);
	}

	public Mono<Void> updateUserEmail(String uid, String email) {
		return Mono.fromCallable(() -> new UserRecord.UpdateRequest(uid).setEmail(email).setEmailVerified(false))
				.subscribeOn(Schedulers.boundedElastic())
				.flatMap(request -> Mono.fromCallable(() -> {
									FirebaseAuth.getInstance().updateUser(request);
									return null;
								})
								.subscribeOn(Schedulers.boundedElastic())
				)
				.onErrorMap(FirebaseAuthException.class, e -> {
					ErrorCode errorCode = e.getErrorCode();
                    return switch (errorCode) {
                        case INVALID_ARGUMENT -> new RuntimeException("Invalid email format: " + email, e);
                        case ALREADY_EXISTS -> new RuntimeException("Email is already in use: " + email, e);
                        case NOT_FOUND -> new RuntimeException("User not found for UID: " + uid, e);
                        case PERMISSION_DENIED -> new RuntimeException("Check Admin SDK credentials", e);
                        case UNAUTHENTICATED -> new RuntimeException("Invalid or missing token", e);
                        case INTERNAL, UNKNOWN -> new RuntimeException("Firebase server error", e);
                        default -> new RuntimeException("Unexpected Firebase error: " + errorCode, e);
                    };
				})
				.then();
	}

	public Boolean createUser(UserRecord.CreateRequest createRequest) {
		try {
			FirebaseAuth.getInstance().createUser(createRequest);
			return true;
		} catch (FirebaseAuthException e) {
			throw new RuntimeException("Error deleting auth user: ", e);
		}
	}
}
