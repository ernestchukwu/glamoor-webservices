package uk.co.glamoor.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import reactor.core.publisher.Mono;
import uk.co.glamoor.gateway.filter.FirebaseAuthGatewayFilter;
import uk.co.glamoor.gateway.filter.SecurityHeadersFilter;
import uk.co.glamoor.gateway.filter.ServiceUnavailableFilter;
import uk.co.glamoor.gateway.filter.SqlInjectionFilter;

@Configuration
public class RouteLocatorConfig {

    private final FirebaseAuthGatewayFilter firebaseAuthFilter;

    public RouteLocatorConfig(FirebaseAuthGatewayFilter firebaseAuthGatewayFilter) {
        this.firebaseAuthFilter = firebaseAuthGatewayFilter;
    }

    @Bean
    @Primary
    KeyResolver userKeyResolver() {
        return exchange -> Mono.just(exchange
                .getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

    @Bean
    RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 20);
    }

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder, ServiceUnavailableFilter serviceUnavailableFilter) {
        return builder.routes()
                .route("booking-service", r -> r.path("/api/bookings/**")
                        .filters(f -> f.filter(firebaseAuthFilter)
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                                .setKeyResolver(userKeyResolver()))
                                .filter(new SqlInjectionFilter())
                                .filter(new SecurityHeadersFilter())
                                .filter(serviceUnavailableFilter))
                        .uri("lb://bookings"))

                .route("customer-service", r -> r.path("/api/customers/**")
                        .filters(f -> f.filter(firebaseAuthFilter)
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                                .setKeyResolver(userKeyResolver()))
                                .filter(new SqlInjectionFilter())
                                .filter(new SecurityHeadersFilter())
                                .filter(serviceUnavailableFilter))
                        .uri("lb://customers"))
                        
                .route("notification-service", r -> r.path("/api/notifications/**")
                        .filters(f -> f.filter(firebaseAuthFilter)
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                                .setKeyResolver(userKeyResolver()))
                                .filter(new SqlInjectionFilter())
                                .filter(new SecurityHeadersFilter())
                                .filter(serviceUnavailableFilter))
                        .uri("lb://notifications"))
                        
                .route("payment-service", r -> r.path("/api/payments/**")
                        .filters(f -> f.filter(firebaseAuthFilter)
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                                .setKeyResolver(userKeyResolver()))
                                .filter(new SqlInjectionFilter())
                                .filter(new SecurityHeadersFilter())
                                .filter(serviceUnavailableFilter))
                        .uri("lb://payments"))

                .route("post-service", r -> r.path("/api/posts/**")
                        .filters(f -> f.filter(firebaseAuthFilter)
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                                .setKeyResolver(userKeyResolver()))
                                .filter(new SqlInjectionFilter())
                                .filter(new SecurityHeadersFilter())
                                .filter(serviceUnavailableFilter))
                        .uri("lb://posts"))

                .route("review-service", r -> r.path("/api/reviews/**")
                        .filters(f -> f.filter(firebaseAuthFilter)
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                                .setKeyResolver(userKeyResolver()))
                                .filter(new SqlInjectionFilter())
                                .filter(new SecurityHeadersFilter())
                                .filter(serviceUnavailableFilter))
                        .uri("lb://reviews"))

                .route("service-service", r -> r.path("/api/services/**")
                        .filters(f -> f.filter(firebaseAuthFilter)
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                                .setKeyResolver(userKeyResolver()))
                                .filter(new SqlInjectionFilter())
                                .filter(new SecurityHeadersFilter())
                                .filter(serviceUnavailableFilter))
                        .uri("lb://services"))

                .route("stylist-service", r -> r.path("/api/stylists/**")
                        .filters(f -> f.filter(firebaseAuthFilter)
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                                .setKeyResolver(userKeyResolver()))
                                .filter(new SqlInjectionFilter())
                                .filter(new SecurityHeadersFilter())
                                .filter(serviceUnavailableFilter))

                        .uri("lb://stylists"))
                        
                .build();
    }
}
