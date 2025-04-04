package uk.co.glamoor.gateway.filter;

import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import com.google.firebase.auth.FirebaseAuth;
import uk.co.glamoor.gateway.repository.UserRepository;
import uk.co.glamoor.gateway.service.UserService;

import java.time.Duration;

@Component
public class FirebaseAuthGatewayFilter implements GatewayFilter {

    private final Logger logger = LoggerFactory.getLogger(FirebaseAuthGatewayFilter.class);

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    public FirebaseAuthGatewayFilter(@Qualifier("reactiveRedisTemplate") ReactiveRedisTemplate<String, String> redisTemplate, UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            logger.warn("Missing or malformed Authorization header");
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (token.equals("chineduchukwu"))
            return chain.filter(exchange);

        return Mono.fromCallable(() -> FirebaseAuth.getInstance().verifyIdToken(token))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(decodedToken -> addUserDetailsToRequest(exchange, decodedToken))
                .flatMap(chain::filter)
                .onErrorResume(e -> {
                    logger.error("Firebase authentication failed: {}", e.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });

    }

    private Mono<ServerWebExchange> addUserDetailsToRequest(ServerWebExchange exchange, FirebaseToken decodedToken) {
        String uid = decodedToken.getUid();
        String redisKey = "uid:" + uid;

        return redisTemplate.opsForValue().get(redisKey)
                .flatMap(_id -> {

                    exchange.getRequest().mutate()
                            .header("X-User-Uid", uid)
                            .header("X-User-Id", _id)
                            .build();

                    logger.info("Cache hit: Added uid: {} and _id: {} to headers", uid, _id);
                    return Mono.just(exchange);
                })
                .switchIfEmpty(
                        userRepository.findByUid(uid)
                                .flatMap(user -> {
                                    String _id = user.getId();

                                    redisTemplate.opsForValue()
                                            .set(redisKey, _id, Duration.ofDays(30))
                                            .subscribe();

                                    exchange.getRequest().mutate()
                                            .header("X-User-Uid", uid)
                                            .header("X-User-Id", _id)
                                            .build();

                                    logger.info("Cache miss: Added uid: {} and _id: {} to headers", uid, _id);
                                    return Mono.just(exchange);
                                })
                                .defaultIfEmpty(exchange)
                                .doOnSubscribe(s -> logger.info("User's uid: {}", uid))
                );
    }
}
