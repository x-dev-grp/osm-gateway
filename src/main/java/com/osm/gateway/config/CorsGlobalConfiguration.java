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
        return (exchange, chain) -> {
            String origin = exchange.getRequest().getHeaders().getOrigin();
            var response   = exchange.getResponse();

            if (origin != null && List.of(
                    "https://osm-ms-fe.onrender.com",
                    "http://localhost:4200"
            ).contains(origin)) {

                // 🔑  make sure there is ONE and only ONE value
                response.getHeaders().remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
                response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                        "GET, POST, PUT, DELETE, OPTIONS");
                response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                        "Authorization, Content-Type");
                response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            }

            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return response.setComplete();
            }
            return chain.filter(exchange);
        };
    }

}
