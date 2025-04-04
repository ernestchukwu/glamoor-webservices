package uk.co.glamoor.stylists.service;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

@Service
public class RequestService {

    public String getIPAddress(ServerWebExchange exchange) {
        String clientIp = "";
        String xForwardedForHeader = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedForHeader != null) {
            clientIp = xForwardedForHeader.split(",")[0].trim();
        } else {
            if (exchange.getRequest().getRemoteAddress() != null)
                clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }
        return clientIp;
    }
}
