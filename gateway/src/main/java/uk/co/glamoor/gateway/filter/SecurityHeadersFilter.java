package uk.co.glamoor.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityHeadersFilter implements GatewayFilter {
    private static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";
    private static final String X_XSS_PROTECTION = "X-XSS-Protection";
    private static final String X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getResponse().getHeaders().add(CONTENT_SECURITY_POLICY, "default-src 'self'");
        exchange.getResponse().getHeaders().add(X_XSS_PROTECTION, "1; mode=block");
        exchange.getResponse().getHeaders().add(X_CONTENT_TYPE_OPTIONS, "nosniff");
        return chain.filter(exchange);
    }
}
