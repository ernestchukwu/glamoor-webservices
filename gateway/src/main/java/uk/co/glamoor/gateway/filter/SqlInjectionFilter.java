package uk.co.glamoor.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@Component
public class SqlInjectionFilter implements GatewayFilter {
    private static final Pattern SQL_PATTERN = Pattern.compile("([';]+|(--)+)");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String queryParams = exchange.getRequest().getURI().getQuery();

        if (queryParams != null && SQL_PATTERN.matcher(queryParams).find()) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }
}
