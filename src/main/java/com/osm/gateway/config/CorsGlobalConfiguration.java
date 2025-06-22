package com.osm.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class CorsGlobalConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CorsGlobalConfiguration.class);

    private static final List<String> allowedOrigins = List.of(
            "https://osm-ms-fe.onrender.com",
            "http://localhost:4200"
    );

    @Bean
    public WebFilter corsFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            String origin = exchange.getRequest().getHeaders().getOrigin();
            var request = exchange.getRequest();
            var response = exchange.getResponse();

            if (origin != null && allowedOrigins.contains(origin)) {
                HttpHeaders headers = response.getHeaders();
                headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin); // ensures single value
                headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS");
                headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Authorization, Content-Type");
                headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");

                logger.info("[CORS] ✅ Allowed origin: {}", origin);
            } else if (origin != null) {
                logger.warn("[CORS] ❌ Blocked origin: {}", origin);
            }

            if (request.getMethod() == HttpMethod.OPTIONS) {
                logger.info("[CORS] Preflight OPTIONS request handled for origin: {}", origin);
                response.setStatusCode(HttpStatus.OK);
                return response.setComplete();
            }

            return chain.filter(exchange);
        };
    }
}
