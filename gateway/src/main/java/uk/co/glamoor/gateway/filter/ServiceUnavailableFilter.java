package uk.co.glamoor.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ServiceUnavailableFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).onErrorResume(Exception.class, ex -> {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
            byte[] errorMessage = "Service Unavailable. Please try again later.".getBytes();
            return response.writeWith(Mono.just(response.bufferFactory().wrap(errorMessage)));
        });
    }
}
