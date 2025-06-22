package com.osm.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class CorsGlobalConfiguration {

    @Bean
    public WebFilter corsFilter() {
        return (exchange, chain) -> {
            String origin = exchange.getRequest().getHeaders().getOrigin();
            ServerHttpResponse response = exchange.getResponse();

            // Allow list for frontend origins (add more if needed)
            List<String> allowedOrigins = List.of(
                    "https://osm-ms-fe.onrender.com",
                    "http://localhost:4200"
            );

            if (origin != null && allowedOrigins.contains(origin)) {
                HttpHeaders headers = response.getHeaders();
                headers.set("Access-Control-Allow-Origin", origin); // set, not add!
                headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                headers.set("Access-Control-Allow-Headers", "Authorization, Content-Type");
                headers.set("Access-Control-Allow-Credentials", "true");
            }

            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return response.setComplete();
            }

            return chain.filter(exchange);
        };
    }
}


