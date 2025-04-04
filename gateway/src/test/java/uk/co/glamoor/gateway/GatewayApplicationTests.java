package uk.co.glamoor.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import uk.co.glamoor.gateway.service.FirebaseService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class GatewayApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@Autowired
    private WebTestClient webTestClient;
	    
    private static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";
    private static final String X_XSS_PROTECTION = "X-XSS-Protection";
    private static final String X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";

    @Autowired
    private FirebaseService firebaseService;

    private String mockToken;
    
    @BeforeEach
    public void setup() {
    	
        mockToken = firebaseService.generateAnonymousToken("test-user-1");
        System.out.println("Mock Token: " + mockToken);
        
    }
    
    @Test
    public void testFirebaseAuthSuccess() throws Exception {
        webTestClient.mutate()
                .defaultHeader("Authorization", "Bearer " + mockToken)
                .build()
                .get()
                .uri("/api/bookings")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.uid").isNotEmpty();
    }

    @Test
    public void testFirebaseAuthMissingToken() {
        webTestClient.get()
                .uri("/api/bookings")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Unauthorized");
    }

    @Test
    public void testFirebaseAuthInvalidToken() throws Exception {
        String invalidToken = "invalid_token";

        webTestClient.mutate()
                .defaultHeader("Authorization", "Bearer " + invalidToken)
                .build()
                .get()
                .uri("/api/bookings")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Unauthorized");
    }

    @Test
    public void testSecurityHeaders() {
        webTestClient.get()
                .uri("/api/bookings")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists(CONTENT_SECURITY_POLICY)
                .expectHeader().exists(X_XSS_PROTECTION)
                .expectHeader().exists(X_CONTENT_TYPE_OPTIONS);
    }

    @Test
    public void testSqlInjectionPrevention() {
        String maliciousQuery = "/api/bookings?q=test';";

        webTestClient.get()
                .uri(maliciousQuery)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isNotEmpty();
    }

    @Test
    public void testRateLimiting() {
        for (int i = 0; i <= 10; i++) { 
            webTestClient.get()
                    .uri("/api/bookings")
                    .exchange();
        }

        webTestClient.get()
                .uri("/api/bookings")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Rate Limit Exceeded");
    }

}
